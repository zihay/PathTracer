package ray.renderer;

import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class BruteForceRRPathTracer extends PathTracer {
    protected double survivalProbability = 0.5;

    public void setSurvivalProbability(double val) {
    	this.survivalProbability = val; 
    	System.out.println("SET: " + survivalProbability);
    }

    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // The function should be the same as BruteForcePathTracer *except* the termination 
    	// condition. Here please use Russian Roulette to terminate the recursive call.
    	// The survival probability of Russian Roulette is set in the XML file.
        Point2 seed = new Point2();
        sampler.sample(2, sampleIndex, seed);
        if(seed.x > survivalProbability){
            outColor.set(0.0);
            return;
        }
        IntersectionRecord iRec = new IntersectionRecord();
        if (scene.getFirstIntersection(iRec, ray)) {
//            Vector3 tmp = new Vector3(iRec.frame.o);
//            Vector3 cc = new Vector3(0 ,0.8, 1);
//            tmp.sub(cc);
//            if(tmp.length()>0.7999 && tmp.length()<0.80001) {
//                System.out.println(level);
//                System.out.println("incircle");
//            }
            Color emit = new Color();
            Vector3 outDir = ray.direction;
            outDir = new Vector3(-outDir.x, -outDir.y, -outDir.z);
            emittedRadiance(iRec, outDir, emit);
            Point2 directSeed = new Point2();
            sampler.sample(1, sampleIndex, directSeed);
            Color illumination = new Color();
            gatherIllumination(scene,outDir,iRec,sampler,sampleIndex,level,illumination);
            outColor.set(emit);
            outColor.add(illumination);
        }else{
//            outColor.set(0);
//            outColor.set(backgroundIllumination);
            Vector3 outDir = ray.direction;
            outDir = new Vector3(-outDir.x, -outDir.y, -outDir.z);
            scene.getBackground().evaluate(outDir,outColor);
    }
    }

}
