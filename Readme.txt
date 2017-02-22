Ceci est la page d'acceuil
A implémenter :
fumigène
mitrailleuse
canon 
infantrie
hélicoptères
Pathfinding:
Map = fichier json contenant les points des noeuds du graphe (coord carthésiennes) + les obstacles fixes (leurs centre + les points formant leur shape)
au chargement de la map , on applique Fortune's algorithm pour associer a chaque point sa cellule et ensuite  The Delaunay Triangulation pour former le graphe de la map

