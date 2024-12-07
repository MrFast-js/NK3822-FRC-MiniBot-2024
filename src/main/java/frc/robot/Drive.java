package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonFX;

public class Drive {
    private static PWMTalonFX left_motor = new PWMTalonFX(1);
    private static PWMTalonFX right_motor = new PWMTalonFX(0);
    private static DifferentialDrive diffdrive = new DifferentialDrive(left_motor, right_motor);

    public static void control(double rightSpeed, double leftSpeed, double acceleration) {
        // System.out.println("Left Speed: " + rightSpeed + " Right Speed: " + leftSpeed + " Acceleration: " + acceleration);
        diffdrive.tankDrive(rightSpeed * acceleration, -leftSpeed * acceleration);
    }
}
