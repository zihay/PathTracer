package ray.renderer;

import ray.brdf.BRDF;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.*;


/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * <p>
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 *
 * @author Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {


    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
                                   IntersectionRecord iRec, Point2 seed, Color outColor) {
        // W4160 TODO (A)
        // This method computes a Monte Carlo estimate of reflected radiance due to direct illumination.  It
        // generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        //
        // The same code could be interpreted as an integration wrt. solid angle, as follows:
        //
        //    f = brdf * radiance * cos_theta
        //    p = cos_theta / pi
        //    g = f / p = brdf * radiance * pi
        //
        // As a hint, here are a few steps when I code this function
        // 1. Generate a random incident direction according to proj solid angle
        //    pdf is constant 1/pi
        // 2. Find incident radiance from that direction
        // 3. Estimate reflected radiance using brdf * radiance / pdf = pi * brdf * radiance

        BRDF brdf = iRec.surface.getMaterial().getBRDF(iRec);
        Color brdfValue = new Color();
//        Color outweight = new Color();
//        brdf.generate(iRec.frame, outDir, incDir, seed, outweight);
//        brdf.evaluate(iRec.frame, incDir, outDir, brdfValue);

//        Color brdf = new Color(0.5, 0.5, 0.5);
//        brdf.scale(1 / Math.PI);

        Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);
        brdf.evaluate(iRec.frame, incDir, outDir, brdfValue);
//        brdfValue.scale(1 / Math.PI);
        Ray shadowRay = new Ray(iRec.frame.o, incDir);
        shadowRay.makeOffsetRay();

        Color radiance = new Color();
        if (!scene.getFirstIntersection(iRec, shadowRay)) {
            radiance.set(0.);
        } else {
            if (iRec.surface.getMaterial().isEmitter()) {
                LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
                lRec.set(iRec);
                iRec.surface.getMaterial().emittedRadiance(lRec, radiance);
            } else {
                radiance.set(0.);
            }
        }

        outColor.set(brdfValue);
        outColor.scale(radiance);
        outColor.scale(Math.PI);
    }

}
