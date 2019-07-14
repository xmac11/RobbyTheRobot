package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.PPM;

public class Parallax extends Actor {

    private Array<Texture> layers;

    float x,y,width,heigth,scaleX,scaleY;
    float x2;
    float temp;
    int originX, originY,rotation,srcX,srcY;
    boolean flipX,flipY;

    private Viewport viewport;
    private Camera camera;
    private Robot robot;

    private float previous;
    private float counter;
    private boolean flag;
    private float n;
    private  float accumulator;

    public Parallax(Viewport viewport, Robot robot, Array<Texture> textures) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
        this.robot = robot;
        this.layers = textures;

        x = camera.position.x - viewport.getWorldWidth() / 2;
        x2 = camera.position.x + viewport.getWorldWidth() / 2;
        y = originX = originY = rotation = srcY = 0;
        width =  Gdx.graphics.getWidth() / PPM;
        heigth = Gdx.graphics.getHeight() / PPM;
        scaleX = scaleY = 1;
        flipX = flipY = false;

    }

    public boolean wrapNeeded() {
        n = x + viewport.getWorldWidth() - camera.position.x + viewport.getWorldWidth() / 2;
        return  Math.abs(n) > viewport.getWorldWidth();
//        return counter > viewport.getWorldWidth();
    }

    public void wrapAround() {
//        x = x2;
//        x2 += viewport.getWorldWidth();

        x += 2 * viewport.getWorldWidth();
        n = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

       /* if(robot.getBody().getPosition().x < camera.position.x)
            x = camera.position.x - robot.getBody().getPosition().x + 32 / PPM;
        else*/
            x = camera.position.x - viewport.getWorldWidth() / 2;
            x2 = camera.position.x + viewport.getWorldWidth() /2;

        x *= 0.8;
        x2 *= 0.8;

//        x2 = x + viewport.getWorldWidth();

//        System.out.println("x: " + x);
//        System.out.println("x2: " + x2);
//        System.out.println("Camera: " + camera.position.x);
//        System.out.println(x + viewport.getWorldWidth() < camera.position.x - viewport.getWorldWidth() / 2);
//        System.out.println(x + viewport.getWorldWidth() - camera.position.x + viewport.getWorldWidth() / 2);

        float current = x + viewport.getWorldWidth() - camera.position.x + viewport.getWorldWidth() / 2;
        float delta = current - previous;
        previous = current;

        accumulator += delta;
        System.out.println(accumulator);

        /*if(accumulator > viewport.getWorldWidth()) {
            wrapAround();
            accumulator = viewport.getWorldWidth();
        }*/


        /*if(counter > viewport.getWorldWidth()) {
            counter = 0;
        }
        else {
            counter += camera.position.x - previous;
        }
        previous = camera.position.x;*/

//        System.out.println("Counter: " + counter);


        /*if(wrapNeeded()) {
            wrapAround();
            flag = true;
        }*/

//        System.out.println("x: " + x);
//        System.out.println("x2: " + x2);
//        System.out.println("Camera: " + camera.position.x);
        for(int i = 0; i < layers.size; i++) {
            batch.draw(layers.get(i), x, camera.position.y - viewport.getWorldHeight() / 2,
                    originX, originY, width, heigth,scaleX,scaleY,rotation,srcX,srcY,
                    layers.get(i).getWidth(), layers.get(i).getHeight(), flipX,flipY);
            /*batch.draw(layers.get(i), x + viewport.getWorldWidth(), camera.position.y - viewport.getWorldHeight() / 2,
                    originX, originY, width, heigth,scaleX,scaleY,rotation,srcX,srcY,
                    layers.get(i).getWidth(), layers.get(i).getHeight(), flipX,flipY);*/
        }

        if(flag) {

        }
    }
}
