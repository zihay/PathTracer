package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

import java.util.concurrent.ThreadLocalRandom;

public abstract class PathTracer extends DirectIlluminationRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
    }

    public void setBackgroundIllumination(int backgroundIllumination) {
        this.backgroundIllumination = backgroundIllumination;
    }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {

        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir,
                                   IntersectionRecord iRec, SampleGenerator sampler,
                                   int sampleIndex, int level, Color outColor) {
        // W4160 TODO (B)
        //
        // This method computes a Monte Carlo estimate of reflected radiance due to direct and/or indirect 
        // illumination.  It generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        // You need:
        //   1. Generate a random incident direction according to proj solid angle
        //      pdf is constant 1/pi
        //   2. Recursively find incident radiance from that direction
        //   3. Estimate the reflected radiance: brdf * radiance / pdf = pi * brdf * radiance
        //
        // Here you need to use Geometry.squareToPSAHemisphere that you implemented earlier in this function

        BRDF brdf = iRec.surface.getMaterial().getBRDF(iRec);
        Color brdfValue = new Color();
        Vector3 incDir = new Vector3();
        Point2 directSeed = new Point2();
        int randnum = ThreadLocalRandom.current().nextInt(0,sampler.getNumSamples());
        sampler.sample(randnum, sampleIndex, directSeed);
        Geometry.squareToPSAHemisphere(directSeed, incDir);
        iRec.frame.canonicalToFrame(outDir);
//        outDir.scale(-1);
        brdf.evaluate(iRec.frame, incDir, outDir, brdfValue);
//        brdfValue.scale(1 / Math.PI);
        double p = brdf.pdf(iRec.frame, outDir, incDir);
        iRec.frame.frameToCanonical(incDir);
        Ray reflectRay = new Ray(iRec.frame.o, incDir);
        reflectRay.makeOffsetRay();

        Color radiance = new Color();
        rayRadianceRecursive(scene, reflectRay, sampler, sampleIndex, level + 1, radiance);

        outColor.set(brdfValue);
        outColor.scale(radiance);
//        outColor.scale(Math.PI);
        outColor.scale(1/p);
    }
}
