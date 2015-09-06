/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;


public final class Trans {

    public static void put( Trans3 a, Trans3 out ) {
        Vec.put( a.mPos, out.mPos );
        Mat.put( a.mRot, out.mRot );
    }


    public static void identity( Trans3 a ) {
        Vec.put( 0, 0, 0, a.mPos );
        Mat.identity( a.mRot );
    }


    public static void mult( Trans3 a, Vec3 vec, Vec3 out ) {
        Vec3 p = a.mPos;
        float x = p.x;
        float y = p.y;
        float z = p.z;
        Mat.mult( a.mRot, vec, out );
        out.x += x;
        out.y += y;
        out.z += z;
    }


    public static void mult( Trans3 a, Vec4 vec, Vec4 out ) {
        Vec3 p = a.mPos;
        float vw = vec.w;
        float x = p.x;
        float y = p.y;
        float z = p.z;
        Mat.mult( a.mRot, vec, out );
        out.x += vw * x;
        out.y += vw * y;
        out.z += vw * z;
    }


    public static void mult( Trans3 a, Trans3 b, Trans3 out ) {
        mult( a, b.mPos, out.mPos );
        Mat.mult( a.mRot, b.mRot, out.mRot );
    }


    public static void invert( Trans3 tr, Trans3 out ) {
        Mat.invert( tr.mRot, out.mRot );
        Vec3 p = out.mPos;
        Mat.mult( out.mRot, tr.mPos, p );
        p.x = -p.x;
        p.y = -p.y;
        p.z = -p.z;
    }


    public static void orthoInvert( Trans3 tr, Trans3 out ) {
        Mat.transpose( tr.mRot, out.mRot );
        Vec3 p = out.mPos;
        Mat.mult( out.mRot, tr.mPos, p );
        p.x = -p.x;
        p.y = -p.y;
        p.z = -p.z;
    }


    public static void matToTrans( Mat4 a, Trans3 out ) {
        Mat.put( a, out.mRot );
        Vec.put( a.m03, a.m13, a.m23, out.mPos );
    }


    public static void transToMat( Trans3 tr, Mat4 out ) {
        final Mat3 rot = tr.mRot;
        out.m00 = rot.m00;
        out.m10 = rot.m10;
        out.m20 = rot.m20;
        out.m30 = 0;
        out.m01 = rot.m01;
        out.m11 = rot.m11;
        out.m21 = rot.m21;
        out.m31 = 0;
        out.m02 = rot.m02;
        out.m12 = rot.m12;
        out.m22 = rot.m22;
        out.m32 = 0;
        out.m03 = tr.mPos.x;
        out.m13 = tr.mPos.y;
        out.m23 = tr.mPos.z;
        out.m33 = 1;
    }


    public static void lerp( Trans3 a, Trans3 b, float t, Vec4 workA, Vec4 workB, Trans3 out ) {
        Vec.lerp( a.mPos, b.mPos, t, out.mPos );
        Mat.slerp( a.mRot, b.mRot, t, workA, workB, out.mRot );
    }


    public static String format( Trans3 tr ) {
        StringBuilder sb = new StringBuilder();
        Mat3 mat = tr.mRot;
        Vec3 pos = tr.mPos;
        sb.append( "[" );
        sb.append( String.format( Vec.FORMAT4, mat.m00, mat.m01, mat.m02, pos.x ) ).append( '\n' );
        sb.append( String.format( Vec.FORMAT4, mat.m10, mat.m11, mat.m12, pos.y ) ).append( '\n' );
        sb.append( String.format( Vec.FORMAT4, mat.m20, mat.m21, mat.m22, pos.z ) );
        sb.append( "]");
        return sb.toString();
    }

}
