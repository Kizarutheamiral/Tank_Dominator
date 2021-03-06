package com.dominator.game.Entities;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.dominator.game.Quadtree.Node;
import java.util.*;
import java.util.List;

import static com.dominator.game.CONSTANT.scale;


/**
 * Created by Choujaa Wassil on 22/02/2017.
 *
 */
public class JsonToBody {

    public final Model model = new Model();
    // Reusable stuff
    private final List<Vector2> vectorPool = new ArrayList<Vector2>();
    private final Vector2 vec = new Vector2();

    // -------------------------------------------------------------------------
    // Ctors
    // -------------------------------------------------------------------------

    public JsonToBody(FileHandle file, float scale) {
        if (file == null) throw new NullPointerException("file is null");
        readJson(file.readString(), scale);
    }

    public JsonToBody(String str, float scale) {
        if (str == null) throw new NullPointerException("str is null");
        readJson(str, scale);
    }

    public JsonToBody() {

    }

    public void loadFromJSon(FileHandle file, int scale){
        if (file == null) throw new NullPointerException("file is null");
        readJson(file.readString(), scale);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Creates and applies the fixtures defined in the editor. The name
     * parameter is used to retrieve the right fixture from the loaded file.
     * <br/><br/>
     *
     * The body reference point (the red cross in the tool) is by default
     * located at the bottom left corner of the image. This reference point
     * will be put right over the BodyDef position point. Therefore, you should
     * place this reference point carefully to let you place your body in your
     * world easily with its BodyDef.position point. Note that to draw an image
     * at the position of your body, you will need to know this reference point
     * (see {@link #getOrigin(String, float)}.
     * <br/><br/>
     *
     * Also, saved shapes are normalized. As shown in the tool, the width of
     * the image is considered to be always 1 meter. Thus, you need to provide
     * a scale factor so the polygons get resized according to your needs (not
     * every body is 1 meter large in your game, I guess).
     *
     * @param body The Box2d body you want to attach the fixture to.
     * @param name The name of the fixture you want to load.
     * @param fd The fixture parameters to apply to the created body fixture.
     * @param scale The desired scale of the body. The default width is 1.
     */
    public RigidBodyModel attachFixture( String name, float scale) {

        Array<FixtureDef> list = new Array<FixtureDef>();

        RigidBodyModel rbModel = model.rigidBodies.get(name);

        if (rbModel==null){
            return null;
        }

        rbModel.maxX = 0f;
        rbModel.maxY = 0f;

        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        Vector2 origin = vec.set(rbModel.origin).scl(scale);

        FixtureDef fd;

        for (int i=0, n=rbModel.polygons.size(); i<n; i++) {
            fd = new FixtureDef();

            PolygonModel polygon = rbModel.polygons.get(i);
            Vector2[] vertices = polygon.buffer;

            for (int ii=0, nn=vertices.length; ii<nn; ii++) {
                vertices[ii] = newVec().set(polygon.vertices.get(ii)).scl(scale);
                vertices[ii].sub(origin);

                if(vertices[ii].x>=rbModel.maxX){
                    rbModel.maxX= vertices[ii].x;
                }
                if(vertices[ii].y>=rbModel.maxY){
                    rbModel.maxY= vertices[ii].y;
                }
            }


            PolygonShape s  = new PolygonShape();
            s.set(vertices);
            fd.shape = s;
            list.add(fd);
            for (int ii=0, nn=vertices.length; ii<nn; ii++) {
                free(vertices[ii]);
            }
        }

        float dstX ;
        float dstY ;

        for (int i=0, n=rbModel.circles.size(); i<n; i++) {
             fd = new FixtureDef();

            CircleModel circle = rbModel.circles.get(i);
            Vector2 center = newVec().set(circle.center).scl(scale);
            float radius = circle.radius * scale;
            dstX = center.x + radius;
            dstY = center.y + radius;

            if(dstX >= rbModel.maxX){
                rbModel.maxX = dstX;
            }
            if(dstY>= rbModel.maxY){
                rbModel.maxY=dstY;
            }


            CircleShape s  = new CircleShape();
            s.setPosition(center);
            s.setRadius(radius);
            fd.shape = s;
            list.add(fd);
            free(center);
        }

        rbModel.fixtures.addAll(list);

        return rbModel;
    }

    /**
     * Gets the image path attached to the given name.
     */
    public String getImagePath(String name) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return rbModel.imagePath;
    }

    /**
     * Gets the origin point attached to the given name. Since the point is
     * normalized in [0,1] coordinates, it needs to be scaled to your body
     * size. Warning: this method returns the same Vector2 object each time, so
     * copy it if you need it for later use.
     */
    public Vector2 getOrigin(String name, float scale) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return vec.set(rbModel.origin).scl(scale).cpy();
    }

    /**
     * <b>For advanced users only.</b> Lets you access the internal model of
     * this loader and modify it. Be aware that any modification is permanent
     * and that you should really know what you are doing.
     */
    public Model getInternalModel() {
        return model;
    }

    // -------------------------------------------------------------------------
    // Json Models All the Json Object are stored in this map
    // -------------------------------------------------------------------------

    public static class Model {
        public final java.util.Map<String, RigidBodyModel> rigidBodies = new HashMap<String, RigidBodyModel>();
    }

    public static class RigidBodyModel {

        Float maxX;
        Float maxY;
        public String name;
        public String imagePath;
        public final Vector2 origin = new Vector2();
        public final List<PolygonModel> polygons = new ArrayList<PolygonModel>();
        public final List<CircleModel> circles = new ArrayList<CircleModel>();
        public final Array<FixtureDef> fixtures = new Array<FixtureDef>();

    }

    public interface ShapeModel{
        boolean contain(float x, float y);
        boolean intersect(Node node);
        float getMinX();
        float getMinY();
        float getMaxX();
        float getMaxY();
    }

    public static class PolygonModel implements ShapeModel {
        public float maxX;
        public float maxY;
        public float minX;
        public float minY;
        public final List<Vector2> vertices = new ArrayList<Vector2>();
        private Vector2[] buffer; // used to avoid allocation in attachFixture()
        @Override
        public boolean contain(float x, float y ){
            // The scale of the polygon was set to 1 (in meters) so if your position is in pixel give the scale in param
            int i;
            int j;
            boolean result = false;
            for (i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
                if ((vertices.get(i).y*scale >= y) != (vertices.get(j).y*scale >= y) &&
                        (x <= (vertices.get(j).x*scale - vertices.get(i).x*scale) * (y - vertices.get(i).y*scale) / (vertices.get(j).y*scale-vertices.get(i).y*scale)
                                + vertices.get(i).x*scale))
                {
                    result = !result;
                }
            }
            return result;
        }

        /// intersection with rectangle check AND overlap check
        @Override
        public boolean intersect(Node node){

            for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
                if(node.LigneIntersects(vertices.get(i).x*scale,vertices.get(i).y*scale,vertices.get(j).x*scale,vertices.get(j).y*scale)){
                    return true;
                }
            }


            if (contain(node.getCenterX() - node.getW() / 2, node.getCenterY() - node.getW() / 2)) return true;
            if (contain(node.getCenterX() - node.getW() / 2, node.getCenterY() + node.getW() / 2)) return true;
            if (contain(node.getCenterX() + node.getW() / 2, node.getCenterY() - node.getW() / 2)) return true;
            if (contain(node.getCenterX() + node.getW() / 2, node.getCenterY() + node.getW() / 2)) return true;
            return false;
        }

        @Override
        public float getMinX() {
            return minX*scale;
        }

        @Override
        public float getMinY() {
            return minY*scale;
        }

        @Override
        public float getMaxX() {
            return maxX*scale;
        }

        @Override
        public float getMaxY() {
            return maxY*scale;
        }
    }

    public static class CircleModel implements ShapeModel {
        public final Vector2 center = new Vector2();
        public float radius;
        @Override
        public boolean contain(float x, float y ){
            if((x -center.x*scale)*(x -center.x*scale) + (y -center.y*scale)*(y -center.y*scale) <= (radius*scale)*(radius*scale)){
               return true;
            }
            return false;
        }
        /// intersection with rectangle check
        @Override
        public boolean intersect(Node node){
            // first check if square inside of it
            return node.CircleIntesects(center.x*scale,center.y*scale,radius*scale) || node.contain(center.x*scale,center.y*scale);
        }

        @Override
        public float getMinX() {
            return (center.x-radius)*scale;
        }

        @Override
        public float getMinY() {
            return (center.y-radius)*scale;
        }

        @Override
        public float getMaxX() {
            return (center.x+radius)*scale;
        }

        @Override
        public float getMaxY() {
            return (center.y+radius)*scale;
        }
    }

    // -------------------------------------------------------------------------
    // Json reading process
    // -------------------------------------------------------------------------

    private Model readJson(String str, float scale) {

        JsonValue rootElem = new JsonReader().parse(str);

        JsonValue bodiesElems =  rootElem.get("rigidBodies");

        JsonValue bodyElem =  bodiesElems.get(0);
        RigidBodyModel rbModel = readRigidBody(bodyElem);
        model.rigidBodies.put(rbModel.name, rbModel);

        attachFixture(rbModel.name, scale);

        return model;
    }

    private RigidBodyModel readRigidBody(JsonValue bodyElem) {
        RigidBodyModel rbModel = new RigidBodyModel();
        rbModel.name =  bodyElem.getString("name");
        rbModel.imagePath =  bodyElem.getString("imagePath");
        JsonValue originElem =  bodyElem.get("origin");
        rbModel.origin.x = Float.valueOf(originElem.getString("x")) ;
        rbModel.origin.y = Float.valueOf(originElem.getString("y"));

        // polygons

        JsonValue polygonsElem = bodyElem.get("polygons");

        float maxX=0;
        float maxY=0;
        float minY=0;
        float minX=0;
        for (int i=0; i<polygonsElem.size; i++) {
            PolygonModel polygon = new PolygonModel();
            rbModel.polygons.add(polygon);

            JsonValue verticesElem =  polygonsElem.get(i);
            for (int ii=0; ii<verticesElem.size; ii++) {
                JsonValue vertexElem =  verticesElem.get(ii);
                float x = (Float.valueOf(vertexElem.getString("x")));
                float y = (Float.valueOf(vertexElem.getString("y")));
                if(x>maxX){
                    maxX=x;
                }
                if (y>maxY){
                    maxY=y;
                }
                if(x<minX){
                    minX=x;
                }
                if (y<minY){
                    minY=y;
                }
                polygon.vertices.add(new Vector2(x, y));
            }
            polygon.maxX=maxX;
            polygon.maxY=maxY;
            polygon.minY=minY;
            polygon.minX=minX;
            polygon.buffer = new Vector2[polygon.vertices.size()];
        }

        // circles

        JsonValue circlesElem =  bodyElem.get("circles");

        for (int i=0; i<circlesElem.size; i++) {
            CircleModel circle = new CircleModel();
            rbModel.circles.add(circle);

            JsonValue circleElem =  circlesElem.get(i);
            circle.center.x = (Float.valueOf(circleElem.getString("cx")));
            circle.center.y =  (Float.valueOf(circleElem.getString("cy")));
            circle.radius =  (Float.valueOf(circleElem.getString("r")));
        }

        return rbModel;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Vector2 newVec() {
        return vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
    }

    private void free(Vector2 v) {
        vectorPool.add(v);
    }

}
