package c2g2.engine.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import c2g2.engine.Utils;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class OBJLoader {
	
	/*
	 * //Code adapted from:
	 * 1) http://stackoverflow.com/questions/22316440/how-to-handle-index-buffer-in-opengl-es-1-1/22316797#22316797
	 * 2) 3D Game Development with LWJGL 3
	 */
    public static Mesh loadMesh(String fileName) throws Exception {
    	//// --- student code ---
    	
        float[] positions = null;
        float[] textCoords = null;
        float[] norms = null;
        int[] indices = null;
        
        //Work begins here.
        
        //Establish lists for holding OBJ information read for file.
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indicesTmp = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        
        
      //your task is to read data from an .obj file and fill in those arrays.
        //the data in those arrays should use following format.
        //positions[0]=v[0].position.x positions[1]=v[0].position.y positions[2]=v[0].position.z positions[3]=v[1].position.x ...
        //textCoords[0]=v[0].texture_coordinates.x textCoords[1]=v[0].texture_coordinates.y textCoords[2]=v[1].texture_coordinates.x ...
        //norms[0]=v[0].normals.x norms[1]=v[0].normals.y norms[2]=v[0].normals.z norms[3]=v[1].normals.x...
        //indices[0]=face[0].ind[0] indices[1]=face[0].ind[1] indices[2]=face[0].ind[2] indices[3]=face[1].ind[0]...(assuming all the faces are triangle face)
        
        
        try {
            List<String> lines = Utils.readAllLines(fileName);
            
            //For each line, determine what kind of data (if any) is there.
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                
                /*
                 * If a vertex is encountered, save it in a vector and add to vertices
                 * list.
                 */
                if (line.startsWith("v ")) {
                    String[] tokens = line.split("[ ]+");
                    
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    continue;
                }

                /*
                 * If a normal is encountered, save it in a vector and add to normals
                 * list.
                 */
                if (line.startsWith("vn ")) {
                    String[] tokens = line.split("[ ]+");
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    continue;
                }

                /*
                 * If a texture is encountered, save it in a vector and add to textures
                 * list.
                 */
                if (line.startsWith("vt")) {
                    String[] tokens = line.split("[ ]+");
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    continue;
                }

                /*
                 * If a face (index) is encountered, save it as a Face obj. and 
                 * add to faces list.
                 * 
                 */
                if (line.startsWith("f ")) {
                    String[] tokens = line.split("[ ]+");
                    
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    
                    continue;
                }
            }
        } catch (Exception ex) {
        	System.out.println("There was an error in loading the mesh: "+ex.getMessage());
        	ex.printStackTrace();
            throw new RuntimeException("couldn't load '" + fileName + "'", ex);
        }
        
        /*
         * vertices, textures, normals, faces
         */
        
        // Once complete, create position array in the order it has been declared
        
        //*3 because of each datum is three dimensional.
        positions = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            positions[i * 3] = pos.x;
            positions[i * 3 + 1] = pos.y;
            positions[i * 3 + 2] = pos.z;
            i++;
        }
        
        //*2 because of each datum is two dimensional for textures.
        textCoords = new float[vertices.size() * 2];
        
        //*3 because of each datum for norms is three dimensional.
        norms = new float[vertices.size() * 3];

        //Now, we will process the indices and get all data lined up.
        for (Face face : faces) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textures, normals,
                		indicesTmp, textCoords, norms);
            }
        }
        
        //Finally, store indices from list to array.
        indices = new int[indicesTmp.size()];
        indices = indicesTmp.stream().mapToInt((Integer v) -> v).toArray();
        
        return new Mesh(positions, textCoords, norms, indices);
    }
    
    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
            List<Vector3f> normList, List<Integer> indicesList,
            float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
}
    
    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private IdxGroup[] idxGroups = new IdxGroup[3];

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
}
    
    
}