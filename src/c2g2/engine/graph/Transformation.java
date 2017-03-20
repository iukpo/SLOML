package c2g2.engine.graph;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

//Added for method transformVector (I. Ukpo)
import org.joml.Vector4f;

import c2g2.engine.GameItem;

public class Transformation {

    private final Matrix4f projectionMatrix;
    
    private final Matrix4f viewMatrix;
    
    private final Matrix4f modelMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
    	
        
        //// --- student code ---
        float aspectRatio = width / height;        
        projectionMatrix.identity();
        
        //Calculate variables necessary for perspective matrix.
        float A;
        float B;
        float tanHalfFOV = (float)Math.tan((double)(fov * 0.5f));
        float focalLength = 1.0f / (tanHalfFOV * aspectRatio);
        float C = 1.0f / tanHalfFOV;
        A = (zFar + zNear) / (zNear - zFar);
        B = (zFar + zFar) * zNear / (zNear - zFar);
        float D = projectionMatrix.m20() * A - projectionMatrix.m30();
        float E = projectionMatrix.m21() * A - projectionMatrix.m31();
        float F = projectionMatrix.m22() * A - projectionMatrix.m32();
        float G = projectionMatrix.m23() * A - projectionMatrix.m33();
        
        //Now, set the projection matrix entries.
        projectionMatrix.m00(projectionMatrix.m00() * focalLength);
        projectionMatrix.m01(projectionMatrix.m01() * focalLength);
        projectionMatrix.m02(projectionMatrix.m02() * focalLength);
        projectionMatrix.m03(projectionMatrix.m03() * focalLength);
        projectionMatrix.m10(projectionMatrix.m10() * C);
        projectionMatrix.m11(projectionMatrix.m11() * C);
        projectionMatrix.m12(projectionMatrix.m12() * C);
        projectionMatrix.m13(projectionMatrix.m13() * C);
        projectionMatrix.m30(projectionMatrix.m20() * B);
        projectionMatrix.m31(projectionMatrix.m21() * B);
        projectionMatrix.m32(projectionMatrix.m22() * B);
        projectionMatrix.m33(projectionMatrix.m23() * B);
        projectionMatrix.m20(D);
        projectionMatrix.m21(E);
        projectionMatrix.m22(F);
        projectionMatrix.m23(G);
        
        //Disallowed...
        //projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
    	
        return projectionMatrix;
    }
    
    /*Student code adapted from GLM math library (function lookAt)
     * (http://glm.g-truc.net/0.9.8/index.html)
     * 
     * Also
     * 
     * XNA 3.0 Game Programming Recipes: A Problem-Solution Approach
     */
    //TODO: add lookAt functionality, then clean up
    public Matrix4f getViewMatrix(Camera camera) {
    	Vector3f cameraPos = camera.getPosition();
    	Vector3f cameraTarget = camera.getTarget();
    	Vector3f up = camera.getUp();
        viewMatrix.identity();
        
        //// --- student code ---
        Vector3f rotation = camera.getRotation();
        
        //Calculate rotated target, rotated up, and final target.
        Matrix4f rotMatrix=new Matrix4f();
        rotMatrix.identity();
        rotMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1.0f, 0.0f, 0.0f));
        rotMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0.0f, 1.0f, 0.0f));
        rotMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));
        
        Vector3f cameraRotatedTarget=transformVector(cameraTarget, rotMatrix);
        Vector3f cameraFinalTarget=new Vector3f(cameraPos.x,cameraPos.y,cameraPos.z);
        cameraFinalTarget.add(cameraRotatedTarget);
        Vector3f cameraRotatedUp=transformVector(up,rotMatrix);
        
        //Disallowed...
        //eye,center,up
        //viewMatrix.lookAt(cameraPos, cameraFinalTarget, cameraRotatedUp);
        
        //Finally, update view matrix with rotated target, rotated up, and final target.
        float directionX = cameraPos.x - cameraFinalTarget.x;
        float directionY = cameraPos.y - cameraFinalTarget.y;
        float directionZ = cameraPos.z - cameraFinalTarget.z;
        float invDirLength = 1.0f / (float)Math.sqrt((double)(directionX * directionX + directionY * directionY + directionZ * directionZ));
        float leftX = cameraRotatedUp.y * (directionZ *= invDirLength) - cameraRotatedUp.z * (directionY *= invDirLength);
        float leftY = cameraRotatedUp.z * (directionX *= invDirLength) - cameraRotatedUp.x * directionZ;
        float leftZ = cameraRotatedUp.x * directionY - cameraRotatedUp.y * directionX;
        float invLeftLength = 1.0f / (float)Math.sqrt((double)(leftX * leftX + leftY * leftY + leftZ * leftZ));
        float upnX = directionY * (leftZ *= invLeftLength) - directionZ * (leftY *= invLeftLength);
        float upnY = directionZ * (leftX *= invLeftLength) - directionX * leftZ;
        float upnZ = directionX * leftY - directionY * leftX;
        viewMatrix.m00(leftX);
        viewMatrix.m01(upnX);
        viewMatrix.m02(directionX);
        viewMatrix.m03(0.0f);
        viewMatrix.m10(leftY);
        viewMatrix.m11(upnY);
        viewMatrix.m12(directionY);
        viewMatrix.m13(0.0f);
        viewMatrix.m20(leftZ);
        viewMatrix.m21(upnZ);
        viewMatrix.m22(directionZ);
        viewMatrix.m23(0.0f);
        viewMatrix.m30(- leftX * cameraPos.x + leftY * cameraPos.y + leftZ * cameraPos.z);
        viewMatrix.m31(- upnX * cameraFinalTarget.x + upnY * cameraFinalTarget.y + upnZ * cameraFinalTarget.z);
        viewMatrix.m32(- directionX * cameraPos.x + directionY * cameraPos.y + directionZ * cameraPos.z);
        viewMatrix.m33(1.0f);
        
        return viewMatrix;
    }
    
    public Matrix4f getModelMatrix(GameItem gameItem){
        Vector3f rotation = gameItem.getRotation();
        Vector3f position = gameItem.getPosition();
        modelMatrix.identity();
        
    	//// --- student code ---
        
       //First, get the translation matrix.
       Matrix4f transMatrix=new Matrix4f();
       transMatrix.identity();
       transMatrix.translate(new Vector3f(position.x,position.y,position.z));
       
       //Then, get the rotation matrix.
       Matrix4f rotMatrix=new Matrix4f();
       rotMatrix.identity();
       rotMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1.0f, 0.0f, 0.0f));
       rotMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0.0f, 1.0f, 0.0f));
       rotMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));
       
       //Then, get the scale matrix.
       Matrix4f scaleMatrix=new Matrix4f();
       scaleMatrix.identity();
       float scale=gameItem.getScale();
       scaleMatrix.scale(scale);
       
       //Finally, multiply T*R*S.
       modelMatrix.mul(transMatrix);
       modelMatrix.mul(rotMatrix);
       modelMatrix.mul(scaleMatrix);
       
        //Return result
        return modelMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(getModelMatrix(gameItem));
    }
    
    //Added for getViewMatrix
    //From https://github.com/Techjar/LEDCubeManager/blob/master/src/main/java/com/techjar/ledcm/util/Util.java
    public static Vector3f transformVector(Vector3f vector, Matrix4f matrix) 
    {
    	Vector4f output=new Vector4f();
    	matrix.transform(new Vector4f(vector.x, vector.y, vector.z, 1.0f),output);
		return new Vector3f(output.x, output.y, output.z);
    }
}