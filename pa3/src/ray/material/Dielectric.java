package ray.material;

import ray.brdf.BRDF;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

public class Dielectric implements Material {
    @Override
    public BRDF getBRDF(IntersectionRecord iRec) {
        return null;
    }

    @Override
    public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {

    }

    @Override
    public boolean isEmitter() {
        return false;
    }
}
