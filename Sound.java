/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsquares2;

/**
 *
 * @author VPC
 */
import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	public static final AudioClip BALL = Applet.newAudioClip(Sound.class.getResource("ball.wav"));
	public static final AudioClip GAMEOVER = Applet.newAudioClip(Sound.class.getResource("resources/sounds/gameover.wav"));
	public static final AudioClip INGAME = Applet.newAudioClip(Sound.class.getResource("resources/sounds/ingame.wav"));
        public static final AudioClip HIT = Applet.newAudioClip(Sound.class.getResource("hit.wav"));
}