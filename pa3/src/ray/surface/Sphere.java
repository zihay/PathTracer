package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

import javax.swing.*;
import java.awt.image.PixelInterleavedSampleModel;
import java.util.Vector;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

    /**
     * Material for this sphere.
     */
    protected Material material = Material.DEFAULT_MATERIAL;

    /**
     * The center of the sphere.
     */
    protected final Point3 center = new Point3();

    /**
     * The radius of the sphere.
     */
    protected double radius = 1.0;

    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {
    }

    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter   The center of the new sphere.
     * @param newRadius   The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }

    public void updateArea() {
        area = 4 * Math.PI * radius * radius;
        oneOverArea = 1. / area;
    }

    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Returns the center of the sphere in the input Point3
     *
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {
        outPoint.set(center);
    }

    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {
        this.center.set(center);
    }

    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }

    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }

    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     * ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        // W4160 TODO (A)
        // In this function, you need to test if the given ray intersect with a sphere.
        // You should look at the intersect method in other classes such as ray.surface.Triangle.java
        // to see what fields of IntersectionRecord should be set correctly.
        // The following fields should be set in this function
        //     IntersectionRecord.t
        //     IntersectionRecord.frame
        //     IntersectionRecord.surface
        //
        // Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
        //       and certain rendering algorithm relies on the length. Therefore, here a ray is a
        //       segment rather than a infinite line. You need to test if the segment is intersect
        //       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.

        double ex = ray.origin.x;
        double ey = ray.origin.y;
        double ez = ray.origin.z;

        double dx = ray.direction.x;
        double dy = ray.direction.y;
        double dz = ray.direction.z;

        double cx = center.x;
        double cy = center.y;
        double cz = center.z;

        Vector3 e = new Vector3(ex, ey, ez);
        Vector3 d = new Vector3(dx, dy, dz);
        Vector3 c = new Vector3(cx, cy, cz);

        double A = d.dot(d);
        Vector3 e_c = new Vector3(e.x - c.x, e.y - c.y, e.z - c.z);
        double B = 2 * d.dot(e_c);
        double C = e_c.dot(e_c)-radius*radius;

        double disc = B * B - 4 * A * C;
        if (disc < 0)
            return false;

        double t1 = (-B - Math.sqrt(disc))/(2*A);
        double t2 = (-B + Math.sqrt(disc))/(2*A);

        if(t1 > ray.start && t1 < ray.end)
            outRecord.t = t1;
        else if(t2 > ray.start && t2 < ray.end)
            outRecord.t = t2;
        else
            return false;
        outRecord.surface = this;
        Point3 o = new Point3();
        o.add(e);
        d.scale(outRecord.t);
        o.add(d);
        outRecord.frame.o = o;
        outRecord.frame.w.set(o.x-c.x,o.y-c.y,o.z-c.z);
        outRecord.frame.initFromW();
        return true;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }

    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);
    }

    public static void main(String[] args) {
        Sphere s = new Sphere();
        s.setCenter(new Point3(0,0,0));
        s.setRadius(10);
        Vector3 dir = new Vector3(1,0,0);
        dir.normalize();
        Ray r = new Ray(new Point3(-10,0,0), dir);
        r.makeOffsetRay();
        IntersectionRecord iRec = new IntersectionRecord();
        s.intersect(iRec,r);
    }
}
