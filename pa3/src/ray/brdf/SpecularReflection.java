package ray.brdf;

import ray.material.Material;
import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

public class SpecularReflection implements BRDF {
    @Override
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir, Color outBRDFValue) {
//        incDir.x = -reflDir.x;
//        incDir.y = -reflDir.y;
//        incDir.z = reflDir.z;
//        outBRDFValue.set(fresnelEvaluate(CosTheta(incDir)));
//        outBRDFValue.scale(reflectance);
//        outBRDFValue.scale(1/Math.abs(CosTheta(incDir)));
        incDir.x = -reflDir.x;
        incDir.y = -reflDir.y;
        incDir.z = reflDir.z;
        outBRDFValue.set(new Color(1,1,1));
    }

    @Override
    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 outDir, Point2 seed, Color outWeight) {

    }

    @Override
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        return 1;
    }

    public Color fresnelEvaluate(double cosThetaI) {
        double F = FrDielectric(cosThetaI, etaI, etaT);
        return new Color(F,F,F);
    }

    public double FrDielectric(double cosThetaI, double etaI, double etaT) {

        cosThetaI = clamp(cosThetaI, -1, 1);
        boolean entering = cosThetaI > 0.f;
        if(!entering) {
            double tem = etaI;
            etaI = etaT;
            etaI = tem;
            cosThetaI = Math.abs(cosThetaI);
        }

        double sinThetaI = Math.sqrt(Math.max(0.f, 1 -cosThetaI * cosThetaI));
        double sinThetaT = etaI/etaT * sinThetaI;
        if (sinThetaI >= 1)
            return 1;

        double cosThetaT = Math.sqrt(Math.max(0, 1-sinThetaT*sinThetaT));

        double Rparl = ((etaT * cosThetaI) - (etaI * cosThetaT)) /
                ((etaT * cosThetaI) + (etaI * cosThetaT));
        double Rperp = ((etaI * cosThetaI) - (etaT * cosThetaT)) /
                ((etaI * cosThetaI) + (etaT * cosThetaT));

        return (Rparl * Rparl + Rperp * Rperp) / 2;
    }

    double CosTheta( Vector3 w) { return w.z; }


    public double clamp(double val, double low, double high) {
        if (val < low) return low;
        else if (val > high) return high;
        else return val;
    }

    public void setReflectance(Color reflectance) { this.reflectance.set(reflectance); }
    public void setEtaI(double etaI) { this.etaI = etaI; }
    public void setEtaT(double etaT) { this.etaT = etaT; }

    private double etaI;
    private double etaT;
    private Color reflectance = new Color(.5,.5,.5);

    public static void main(String[] args) {
        SpecularReflection s = new SpecularReflection();
        s.setEtaI(1);
        s.setEtaT(1000);
        s.setReflectance(new Color(1,1,1));
        System.out.println(s.FrDielectric(1,s.etaI,s.etaT));
    }
}
