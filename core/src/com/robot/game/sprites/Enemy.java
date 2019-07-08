package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import static com.robot.game.util.Constants.PPM;

public class Enemy {

    private Body body;
    private FixtureDef fixtureDef;
    public CatmullRomSpline<Vector2> path;
//    public Array<Vector2> path;
    public Vector2 target;
    public float timer;
    public int point = 0;
    public Vector2 velocity = new Vector2();

    public Enemy(Body body, FixtureDef fixtureDef) {
        System.out.println(Math.atan(1) * MathUtils.radiansToDegrees);
        this.body = body;
        this.fixtureDef = fixtureDef;
        body.setTransform(16 / PPM, 10 / PPM, 0);
        body.createFixture(fixtureDef).setUserData(this);

        this.target = new Vector2();
        this.path = new CatmullRomSpline<>(new Vector2[] { new Vector2(350 / PPM, 350 / PPM),
                new Vector2(350 / PPM, 75 / PPM),
                new Vector2(200 / PPM, 75 / PPM),
                new Vector2(200 / PPM, 350 / PPM) }, true);
        /*this.path = new Array<>();
        path.addAll( new Vector2(300 / PPM, 300 / PPM),
                new Vector2(300 / PPM, 75 / PPM),
                new Vector2(0, 75 / PPM),
                new Vector2(0, 300 / PPM));*/
    }

    public void update(float delta) {
        timer += delta;
        float f = timer / 4f;
        if(f <= 1) {
            Vector2 enemyPosition = body.getWorldCenter();
            path.valueAt(target, f); // this method sets the value of target

            Vector2 positionDelta = new Vector2(target).sub(enemyPosition);

            if(delta > 0.01f)
                body.setLinearVelocity(positionDelta.scl(1/delta));
        }
        else
            timer = 0;

        /*float angle = (float) Math.atan2(path.get(point).y - body.getWorldCenter().y, path.get(point).x - body.getWorldCenter().y);
        velocity.set((float) Math.cos(angle) * 5, (float) Math.sin(angle) * 5);

        body.setTransform(body.getPosition().add(velocity.scl(delta)), 0);

        point = (point++) % path.size;*/
    }
}


