package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.behaviors.Jump;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.entities.Monster;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.PPM;

public class JumpAI {

    public EnemyArriveAI monster;
    public PlayScreen playScreen;
    public Jump.JumpDescriptor<Vector2> jumpDescriptor;
    public Jump<Vector2> jump;
    public Seek<Vector2> seek;

    static final Jump.GravityComponentHandler<Vector2> GRAVITY_COMPONENT_HANDLER = new Jump.GravityComponentHandler<Vector2>() {

        @Override
        public float getComponent (Vector2 vector) {
            return vector.y;
        }

        @Override
        public void setComponent (Vector2 vector, float value) {
            vector.y = value;
        }
    };

    public JumpAI(PlayScreen playScreen, EnemyArriveAI monster) {
        this.playScreen = playScreen;
        this.monster = monster;
        Vector2 takeoffPoint = new Vector2(816 / PPM, 304 / PPM);
        Vector2 landingPoint = new Vector2(880 / PPM, 304 / PPM);

        this.jumpDescriptor = new Jump.JumpDescriptor<>(takeoffPoint, landingPoint);


        Jump.JumpCallback jumpCallback = new Jump.JumpCallback() {

            Jump.JumpDescriptor<Vector2> newJumpDescriptor = new Jump.JumpDescriptor<>(new Vector2(), new Vector2());

            @Override
            public void reportAchievability(boolean achievable) {
                System.out.println("Jump Achievability = " + achievable);
            }

            @Override
            public void takeoff(float maxVerticalVelocity, float time) {
//                monster.getBody().setLinearVelocity(monster.getBody().getLinearVelocity().add(0, maxVerticalVelocity));
                Vector2 targetLinearVelocity = jump.getTarget().getLinearVelocity();
                monster.getBody().setLinearVelocity(newJumpDescriptor.takeoffPosition.set(targetLinearVelocity.x,
                        maxVerticalVelocity));
                System.out.println(monster.getBody().getLinearVelocity());
                monster.setSteeringBehavior(null);
            }
        };

        jump = new Jump<>(monster, jumpDescriptor, playScreen.getWorld().getGravity(), GRAVITY_COMPONENT_HANDLER, jumpCallback)
                .setMaxVerticalVelocity(7.5f)
                .setTakeoffPositionTolerance(.5f)
                .setTakeoffVelocityTolerance(.7f)
                .setTimeToTarget(.1f);

    }

    public void update () {

        // Should the character switch to Jump behavior?
        if (monster.getSteeringBehavior() != jump) {
            if (monster.getPosition().x >= jumpDescriptor.takeoffPosition.x - 64 / PPM
                    && monster.getPosition().x <= jumpDescriptor.landingPosition.x) {
                System.out.println("Switched to Jump behavior. Taking a run up...");
                //				System.out.println("run up length = " + distFromTakeoffPoint);
                //				character.body.setDamping(0, 0);
                //				System.out.println("friction: " + character.body.getFriction());
                //				character.body.setFriction(0);
                System.out.println("owner.linearVelocity = " + monster.getLinearVelocity() + "; owner.linearSpeed = "
                        + monster.getLinearVelocity().len());
                monster.setSteeringBehavior(jump);
            }
        }
        if(monster.getSteeringBehavior() == jump) {
            if(monster.getBody().getPosition().x >= jumpDescriptor.landingPosition.x) {
                monster.setSteeringBehaviorToArrive();
            }
        }

        // Update the character
        //monster.update(GdxAI.getTimepiece().getDeltaTime());
    }
}
