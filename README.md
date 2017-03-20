# SLOML (Simple LWJGL OBJ Model Loader)

SLOML is a simple Wavefront (.OBJ) loader written in Java for the Lightweight Java Game Library (LWJGL). This program can be used as an OBJ viewer, or incorporated into other LWJGL projects to provide OBJ mesh loading. It is the product of my first assignment in my Spring 2017 Computer Graphics course at Columbia University, COMS W4160.

After loaded into memory, the mesh can be scaled, rotated, translated, and reflected along the X-axis (flipped upside down). Here is the keymap for interacting with the mesh:

*E: Zoom viewer into mesh.
*R: Zoom viewer out of the mesh.
*T: Move the mesh right.
*Y: Move the mesh left.
*U: Move the mesh up.
*I: Move the mesh down.
*O: Move the mesh towards the viewer.
*P: Move the mesh away from the viewer.
*A: Rotate the mesh towards viewer around the X-axis.
*S: Rotate the mesh away the viewer around the X-axis.
*D: Rotate the mesh to the right around the Y-axis.
*F: Rotate the mesh to the left around the Y-axis.
*G: Rotate the mesh upwards on the Z-axis.
*H: Rotate the mesh downwards on the Z-axis.
*7: Reflect the mesh along the X-axis (flip it upside down. Mind, the keys are a little "sticky" at times, hence the flickering of the model, especially in reflection).

You can also take screenshots by pressing "1".


You may have to adjust scale and position of the OBJ in the init method in DummyGame.java to get the model to be visible and fit entirely into the window. Please see the code of this method for more information. One workaround for this would be to extend the code to allow the program to take the necessary 3D coordinates and scale amount at runtime-this has not been yet implemented.

SLOML was written with Eclipse Neon and requires JOML, LWJGL, and PNGJ libraries, which have been included.
