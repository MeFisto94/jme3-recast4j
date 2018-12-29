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

import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
import org.recast4j.recast.geom.InputGeomProvider;
import org.recast4j.recast.geom.SimpleInputGeomProvider;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class will build a GeometryProvider for Recast to work with.<br />
 * <b>Note: </b>This code has to be run from the MainThread, but once the Geometry is built, it can be run from every
 * thread
 */
public class GeometryProviderBuilder {
    List<Geometry> geometryList;
    Mesh m;

    private GeometryProviderBuilder(List<Geometry> geometryList) {
        this.geometryList = geometryList;
    }

    protected static List<Geometry> findGeometries(Node node, List<Geometry> geoms) {

        // @TODO: Add a interface "spatial filter"
        for (Spatial spatial : node.getChildren()) {
            if (spatial.getUserData("no_collission") != null) {
                continue; // Leave out Non-Collission
            }

            if (spatial instanceof Geometry) {
                geoms.add((Geometry) spatial);
            } else if (spatial instanceof Node) {
                findGeometries((Node) spatial, geoms);
            }
        }
        return geoms;
    }

    public GeometryProviderBuilder(Node n) {
        this(findGeometries(n, new ArrayList<>()));
    }

    // bypass java's "this only in line 1" limitation
    protected static ArrayList<Geometry> newAndAdd(Geometry g) {
        ArrayList<Geometry> geo = new ArrayList<>(1);
        geo.add(g);
        return geo;
    }

    public GeometryProviderBuilder(Geometry g) {
        this(newAndAdd(g));
    }

    public GeometryProviderBuilder(Mesh m) {
        this(new Geometry("", m));
    }

    protected List<Float> getVertices(Mesh mesh, Vector3f scale) {
        FloatBuffer buffer = mesh.getFloatBuffer(VertexBuffer.Type.Position);
        float[] vertexArray = BufferUtils.getFloatArray(buffer);
        List<Float> vertexList = new ArrayList<>(vertexArray.length);
        for (int i = 0; i < vertexArray.length / 3; ++i) {
            // since we scale to the center (which is 0), just multiply
            vertexList.add(vertexArray[3 * i] * scale.x);
            vertexList.add(vertexArray[3 * i + 1] * scale.y);
            vertexList.add(vertexArray[3 * i + 2] * scale.z);
        }
        return vertexList;
    }

    protected List<Integer> getIndices(Mesh mesh) {
        int[] indices = new int[3];
        Integer[] triangles = new Integer[mesh.getTriangleCount() * 3];

        for (int i = 0; i < mesh.getTriangleCount(); i++) {
            mesh.getTriangle(i, indices);
            triangles[3 * i] = indices[0];
            triangles[3 * i + 1] = indices[1];
            triangles[3 * i + 2] = indices[2];
        }
        // Independent copy so Arrays.asList is garbage collected
        return new ArrayList<>(Arrays.asList(triangles));
    }

    public InputGeomProvider build() {
        m = new Mesh();
        // @TODO: BIG TODO!! Maybe we need to remove the GeometryBatchFactory, as it doesn't seem to take scaling into
        // account, which means we need to manually call getVertices on every sub mesh and then just copy the lists together
        // but that doesn't work nicely
        GeometryBatchFactory.mergeGeometries(geometryList, m);
        return new SimpleInputGeomProvider(getVertices(m, geometryList.get(0).getWorldScale()), getIndices(m));
    }
}
