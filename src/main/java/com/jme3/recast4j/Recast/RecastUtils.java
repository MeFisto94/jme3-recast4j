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
package com.jme3.recast4j.Recast;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
import org.recast4j.detour.PolyDetail;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Class consisting of helper methods to simplify interfacing between recast4j and jMonkeyEngine
 * @author MeFisto94
 */
public class RecastUtils {

    public static Mesh getDebugMesh(PolyDetail[] meshes, float[] detailVerts, int[] detailTris) {
        Mesh mesh = new Mesh();
        List<Geometry> geometryList = new ArrayList<>(meshes.length);

        for (PolyDetail pd: meshes) {
            geometryList.add(new Geometry("", getDebugMesh(pd, detailVerts, detailTris)));
        }

        GeometryBatchFactory.mergeGeometries(geometryList, mesh);
        return mesh;
    }

    public static Mesh getDebugMesh(PolyDetail dmesh, float[] detailVerts, int[] detailTris) {
        FloatBuffer vertices = BufferUtils.createVector3Buffer(detailVerts.length);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(detailTris.length);

        for (int i = 0; i < detailTris.length; ++i) {
            indexBuffer.put(detailTris[i]);
        }

        for (int i = 0; i < detailVerts.length; ++i) {
            vertices.put(detailVerts[i]);
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertices);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indexBuffer);
        mesh.updateBound();
        return mesh;
    }
}
