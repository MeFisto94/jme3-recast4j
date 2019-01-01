/*
 *  MIT License
 *  Copyright (c) 2018 MeFisto94
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.jme3.recast4j.Detour;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
import org.recast4j.detour.MeshData;
import org.recast4j.detour.Poly;
import org.recast4j.detour.PolyDetail;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Class consisting of helper methods to simplify interfacing between recast4j and jMonkeyEngine
 * @author MeFisto94
 */
public class DetourUtils {

    /**
     * Simplifies interfacing with float arrays. Create a new Vector3f instance from a float array.
     * This method does not check the size of the array, if it's too long, it will only read the first 3 values,
     * if it's too short, then a IndexOutOfBounds Exception will be thrown.
     *
     * @see #toFloatArray(Vector3f)
     * @param arr The float array containing the Vector
     * @return the created vector
     */
    public static Vector3f createVector3f(float[] arr) {
        return new Vector3f(arr[0], arr[1], arr[2]);
    }

    /**
     * Simplifies interfacing with float arrays. Create a new float array from a Vector3f instance.
     * This method does nothing else than Vector3f#toArray, but people might expect such a method here and
     * didn't know about that method.
     *
     * @see #createVector3f(float[])
     * @param v The vector to convert.
     * @return the float array
     */
    public static float[] toFloatArray(Vector3f v) {
        return v.toArray(null);
    }
}
