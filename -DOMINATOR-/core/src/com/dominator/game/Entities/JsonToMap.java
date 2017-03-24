package com.dominator.game.Entities;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.dominator.game.Quadtree.Node;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import static com.dominator.game.Entities.Map.scale;


/**
 * Created by Choujaa Wassil on 22/02/2017.
 *
 */
public class JsonToMap {
    private final JsonToMap.Model model;

    // Reusable stuff
    private final List<Vector2> vectorPool = new ArrayList<Vector2>();
    private final PolygonShape polygonShape = new PolygonShape();
    private final CircleShape circleShape = new CircleShape();
    private final Vector2 vec = new Vector2();

    // -------------------------------------------------------------------------
    // Ctors
    // -------------------------------------------------------------------------

    JsonToMap(FileHandle file) {
        if (file == null) throw new NullPointerException("file is null");
        model = readJson(file.readString());
    }

    JsonToMap(String str) {
        if (str == null) throw new NullPointerException("str is null");
        model = readJson(str);
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
     * (see {@link #getOrigin(java.lang.String, float)}.
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
    public JsonToMap.RigidBodyModel attachFixture(Body body, String name, FixtureDef fd, float scale) {

        JsonToMap.RigidBodyModel rbModel = model.rigidBodies.get(name);
        rbModel.maxX = 0f;
        rbModel.maxY = 0f;

        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        Vector2 origin = vec.set(rbModel.origin).scl(scale);

        for (int i=0, n=rbModel.polygons.size(); i<n; i++) {
            JsonToMap.PolygonModel polygon = rbModel.polygons.get(i);
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

            polygonShape.set(vertices);
            fd.shape = polygonShape;
            body.createFixture(fd);

            for (int ii=0, nn=vertices.length; ii<nn; ii++) {
                free(vertices[ii]);
            }
        }

        float dstX ;
        float dstY ;

        for (int i=0, n=rbModel.circles.size(); i<n; i++) {
            JsonToMap.CircleModel circle = rbModel.circles.get(i);
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

            circleShape.setPosition(center);
            circleShape.setRadius(radius);
            fd.shape = circleShape;
            body.createFixture(fd);

            free(center);
        }

        return rbModel;
    }

    /**
     * Gets the image path attached to the given name.
     */
    public String getImagePath(String name) {
        JsonToMap.RigidBodyModel rbModel = model.rigidBodies.get(name);
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
        JsonToMap.RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return vec.set(rbModel.origin).scl(scale).cpy();
    }

    /**
     * <b>For advanced users only.</b> Lets you access the internal model of
     * this loader and modify it. Be aware that any modification is permanent
     * and that you should really know what you are doing.
     */
    public JsonToMap.Model getInternalModel() {
        return model;
    }

    // -------------------------------------------------------------------------
    // Json Models
    // -------------------------------------------------------------------------

    public static class Model {
        public final java.util.Map<String, JsonToMap.RigidBodyModel> rigidBodies = new HashMap<String, JsonToMap.RigidBodyModel>();
    }

    public static class RigidBodyModel {
        Float maxX;
        Float maxY;
        public String name;
        public String imagePath;
        public final Vector2 origin = new Vector2();
        public final List<JsonToMap.PolygonModel> polygons = new ArrayList<JsonToMap.PolygonModel>();
        public final List<JsonToMap.CircleModel> circles = new ArrayList<JsonToMap.CircleModel>();
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

            Rectangle.Float rectangle = new Rectangle.Float(node.getCenterX()-node.getW()/2, node.getCenterY()-node.getW()/2, node.getW(), node.getW());
            for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
                Line2D.Float ligne = new Line2D.Float(vertices.get(i).x*scale,vertices.get(i).y*scale,vertices.get(j).x*scale,vertices.get(j).y*scale);
                if(ligne.intersects(rectangle)){
                    return true;
                }
            }
            if(     contain(node.getCenterX()-node.getW()/2,node.getCenterY()-node.getW()/2) ||
                    contain(node.getCenterX()-node.getW()/2,node.getCenterY()+node.getW()/2) ||
                    contain(node.getCenterX()+node.getW()/2,node.getCenterY()-node.getW()/2) ||
                    contain(node.getCenterX()+node.getW()/2,node.getCenterY()+node.getW()/2)){
                return true;
            }
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
            boolean result = false;
            if((x -center.x*scale)*(x -center.x*scale) + (y -center.y*scale)*(y -center.y*scale) <= (radius*scale)*(radius*scale)){
                result = true;
            }
            return result;
        }
        /// intersection with rectangle check
        @Override
        public boolean intersect(Node node){
            // first check if square inside of it
            Rectangle.Float rectangle = new Rectangle.Float(node.getCenterX()-node.getW()/2, node.getCenterY()-node.getW()/2, node.getW(), node.getW());
            Ellipse2D circle = new Ellipse2D.Float(center.x*scale-radius*scale,center.y*scale-radius*scale,radius*scale*2, radius*scale*2);
            return circle.intersects(rectangle);
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

    private JsonToMap.Model readJson(String str) {
        JsonToMap.Model m = new JsonToMap.Model();
        JsonValue rootElem = new JsonReader().parse(str);
        JsonValue bodiesElems =  rootElem.get("rigidBodies");

        for (int i=0; i<bodiesElems.size; i++) {
            JsonValue bodyElem =  bodiesElems.get(i);
            JsonToMap.RigidBodyModel rbModel = readRigidBody(bodyElem);
            m.rigidBodies.put(rbModel.name, rbModel);
        }

        return m;
    }

    private JsonToMap.RigidBodyModel readRigidBody(JsonValue bodyElem) {
        JsonToMap.RigidBodyModel rbModel = new JsonToMap.RigidBodyModel();
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
            JsonToMap.PolygonModel polygon = new JsonToMap.PolygonModel();
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

        System.out.println("now circles");
        // circles

        JsonValue circlesElem =  bodyElem.get("circles");

        for (int i=0; i<circlesElem.size; i++) {
            JsonToMap.CircleModel circle = new JsonToMap.CircleModel();
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
