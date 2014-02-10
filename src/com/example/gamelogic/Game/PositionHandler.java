package com.example.gamelogic.Game;

/**
 * Created by ngorgi on 2/9/14.
 */
public class PositionHandler {

    public static int getPosition (float azimuth_angle, float pitch_angle, float roll_angle){
        if ((pitch_angle > -100 && pitch_angle < -70) && ( roll_angle > -5 && roll_angle < 12)){


            return 1;
        }
        else if ((-10 <pitch_angle && pitch_angle < 10) && (-10 < roll_angle && roll_angle < 10)){


            return 3;
        }
        else if ((-50 < pitch_angle && pitch_angle < -8) && (-2 < roll_angle && roll_angle < 11)){


            return 2;
        }
        else if ((60 < pitch_angle && pitch_angle < 120) && (-20 < roll_angle && roll_angle < 20)){

            return 4;
        }
        else {
            return 0;
        }
    }

    public static boolean IsStable (float[] data){
        double GetPosition = Math.sqrt(Math.pow(data[0],2)+Math.pow(data[1],2) + Math.pow(data[2],2));
        if (GetPosition < 4) {
            return true;
        }
        else{
            return false;
        }
    }
}
