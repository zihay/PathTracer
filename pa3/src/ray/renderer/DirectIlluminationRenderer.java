package ray.renderer;

import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.*;
import ray.sampling.SampleGenerator;

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 *
 * @author cxz (at Columbia)
 */
public class DirectIlluminationRenderer implements Renderer {

    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;

    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectIlluminationRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }


    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance
     * of a different class of DirectIlluminator.
     *
     * @param direct the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }


    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
        // W4160 TODO (A)
        // In this function, you need to implement your direct illumination rendering algorithm
        //
        // you need:
        // 1) compute the emitted light radiance from the current surface if the surface is a light surface
        // 2) direct reflected radiance from other lights. This is by implementing the function
        //    ProjSolidAngleIlluminator.directIlluminaiton(...), and call direct.directIllumination(...) in this
        //    function here.


        IntersectionRecord iRec = new IntersectionRecord();
        if (scene.getFirstIntersection(iRec, ray)) {
            Color emit = new Color();
            if(iRec.surface.getMaterial().isEmitter()) {
                emittedRadiance(iRec, ray.direction, emit);
            }
            Color reflect = new Color();
            Point2 directSeed = new Point2();
            sampler.sample(1, sampleIndex, directSeed);
            Vector3 outDir = ray.direction;
            outDir = new Vector3(-outDir.x, -outDir.y, -outDir.z);
            direct.directIllumination(scene, outDir, iRec, directSeed, reflect);

            outColor.set(emit);
            outColor.add(reflect);
        }else{
            scene.getBackground().evaluate(ray.direction,outColor);
//            outColor.set(0.5);
        }

    }


    /**
     * Compute the radiance emitted by a surface.
     *
     * @param iRec     Information about the surface point being shaded
     * @param dir      The exitant direction (surface coordinates)
     * @param outColor The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
        // W4160 TODO (A)
        // If material is emitting, query it for emission in the relevant direction.
        // If not, the emission is zero.
        // This function should be called in the rayRadiance(...) method above

        LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
        lRec.set(iRec);
        iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
    }
}
