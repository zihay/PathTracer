package ray.brdf;

import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

public class SpecularTransmission implements BRDF {
    @Override
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir, Color outBRDFValue) {
//        incDir.x = -reflDir.x;
//        incDir.y = -reflDir.y;
//        incDir.z = reflDir.z;
//        outBRDFValue.set(fresnelEvaluate(CosTheta(incDir)));
//        outBRDFValue.scale(T);
//        outBRDFValue.scale(1/Math.abs(CosTheta(incDir)));
        boolean entering = CosTheta(reflDir) > 0;
        double etaI = entering ? etaA : etaB;
        double etaT = entering ? etaB : etaA;
        Vector3 n = new Vector3(0,0,1);
        if(n.dot(reflDir)<0.f)
            n.scale(-1);
        if(!Refract(reflDir, n, etaI/etaT, incDir))
            outBRDFValue.set(0);
//        incDir.set(reflDir);
//        incDir.scale(-1);
//        incDir.set(new Vector3(-reflDir.x,-reflDir.y,reflDir.z));
//        outBRDFValue.set(1);
        outBRDFValue.set(fresnelEvaluate(CosTheta(incDir)));
        outBRDFValue.scale(-1);
        outBRDFValue.add(1);
        outBRDFValue.scale(T);
        outBRDFValue.scale(1/Math.abs(CosTheta(incDir)));
    }

    @Override
    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 outDir, Point2 seed, Color outWeight) {

    }

    @Override
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        return 1;
    }


    public Color fresnelEvaluate(double cosThetaI) {
        double F = FrDielectric(cosThetaI, etaA, etaB);
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

    boolean Refract(Vector3 wi, Vector3 n, double eta, Vector3 wt) {
        double cosThetaI = n.dot(wi);
        double sin2ThetaI = Math.max(0.f, 1.f - cosThetaI * cosThetaI);
        double sin2ThetaT = eta * eta * sin2ThetaI;
        if (sin2ThetaT >= 1) return false;
        double cosThetaT = Math.sqrt(1 - sin2ThetaT);
        wt.set(wi);
        wt.scale(-1);
        wt.scale(eta);
        Vector3 nn = new Vector3(n);
        nn.scale(eta*cosThetaI - cosThetaT);
        wt.add(nn);
        return true;
    }

    public double clamp(double val, double low, double high) {
        if (val < low) return low;
        else if (val > high) return high;
        else return val;
    }


    public void setT(Color T) { this.T.set(T); }
    public void setEtaA(double etaA) { this.etaA = etaA; }
    public void setEtaB(double etaB) { this.etaB = etaB; }


    Color T = new Color(1,1,1);
    double etaA;
    double etaB;
}

