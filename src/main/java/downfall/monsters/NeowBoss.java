package downfall.monsters;

import charbosses.bosses.AbstractCharBoss;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import downfall.downfallMod;
import downfall.actions.NeowRezAction;
import downfall.powers.NeowInvulnerablePower;
import guardian.vfx.SmallLaserEffectColored;
import slimebound.SlimeboundMod;

import java.util.ArrayList;
import java.util.Collections;

public class NeowBoss extends AbstractMonster {

    public static final String ID = downfallMod.makeID("NeowBoss");
    public static final String NAME = CardCrawlGame.languagePack.getCharacterString(ID).NAMES[0];
    private static final float HB_X = -40.0F;
    private static final float HB_Y = -40.0F;
    private static final float HB_W = 700.0F;
    private static final float HB_H = 500.0F;


    private static final float EYE1_X = -130.0F * Settings.scale;
    private static final float EYE1_Y = -50F * Settings.scale;

    private static final float EYE2_X = -20.0F * Settings.scale;
    private static final float EYE2_Y = -50F * Settings.scale;

    private static final float EYE3_X = 80.0F * Settings.scale;
    private static final float EYE3_Y = -50F * Settings.scale;

    private static final float INTENT_X = -210.0F * Settings.scale;
    private static final float INTENT_Y = 280.0F * Settings.scale;

    private static final float DRAWX_OFFSET = 100F * Settings.scale;
    private static final float DRAWY_OFFSET = 30F * Settings.scale;

    private float baseDrawX;

    private int turnNum = 0;

    private int strAmt;
    private int blockAmt;

    private boolean isRezzing;
    public boolean offscreen;
    private boolean movingOffscreen;
    private boolean movingBack;

    private float moveTimer;

    public static NeowBoss neowboss;

    public AbstractCharBoss minion;

    public static int Rezzes = 1;

    public ArrayList<String> bossesToRez = new ArrayList<>();


    public NeowBoss() {
        super(NAME, ID, 150, HB_X, HB_Y, HB_W, HB_H, "images/npcs/neow/skeleton.png");

        this.loadAnimation("images/npcs/neow/skeleton.atlas", "images/npcs/neow/skeleton.json", 1.0F);

        this.drawX += DRAWX_OFFSET;
        this.drawY += DRAWY_OFFSET;

        neowboss = this;

        this.baseDrawX = drawX;

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.damage.add(new DamageInfo(this, 6)); //Eye Beam Damage
        this.damage.add(new DamageInfo(this, 20));  //Scream Damage

        this.strAmt = 2; //Strength Scaling for growth ability
        this.blockAmt = 20; //Block for growth ability

        this.intentOffsetX = INTENT_X;


        //Initialize the boss list with the four
        Rezzes = 1;
        bossesToRez.clear();
        bossesToRez.add("downfall:CharBossIronclad");
        bossesToRez.add("downfall:CharBossSilent");
        bossesToRez.add("downfall:CharBossDefect");
        bossesToRez.add("downfall:CharBossWatcher");

        //Remove any that were not encountered (Colosseum event means you could have seen 3 or 4 in the run)
        for (String bossName : downfallMod.possEncounterList) {
            SlimeboundMod.logger.info("neow checking " + bossName);
            if (bossesToRez.contains(bossName)) {
                SlimeboundMod.logger.info("Found this boss, removing it.");
                bossesToRez.remove(bossName);
            }
        }

        Collections.shuffle(bossesToRez);


    }

    @Override
    public void update() {
        super.update();

        if (movingOffscreen) {
            moveTimer -= Gdx.graphics.getDeltaTime();
            this.drawX = Interpolation.linear.apply(this.baseDrawX + 600F * Settings.scale, this.baseDrawX, moveTimer / 2F);

            if (moveTimer <= 0F) {
                movingOffscreen = false;
                offscreen = true;
            }
        } else if (movingBack) {
            moveTimer -= Gdx.graphics.getDeltaTime();
            this.drawX = Interpolation.linear.apply(this.baseDrawX, this.baseDrawX + 600F * Settings.scale, moveTimer / 2F);

            if (moveTimer <= 0F) {
                movingBack = false;
                offscreen = false;
                this.halfDead = false;
                if (!this.hasPower(NeowInvulnerablePower.POWER_ID)) {
                    AbstractDungeon.getCurrRoom().cannotLose = false;
                }
                this.drawX = this.baseDrawX;
                AbstractCharBoss.boss = null;
                getMove(0);
            }
        }
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new NeowInvulnerablePower(this, 3)));
        AbstractDungeon.getCurrRoom().cannotLose = true;
        isRezzing = false;
        offscreen = false;
        movingOffscreen = false;
        movingBack = false;
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_ENDING");
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new HeartMegaDebuffEffect()));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), 1, true, false, false, (float) Settings.WIDTH * 0.2F, (float) Settings.HEIGHT / 2.0F));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, (float) Settings.WIDTH * 0.35F, (float) Settings.HEIGHT / 2.0F));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Wound(), 1, true, false, false, (float) Settings.WIDTH * 0.5F, (float) Settings.HEIGHT / 2.0F));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, (float) Settings.WIDTH * 0.65F, (float) Settings.HEIGHT / 2.0F));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new VoidCard(), 1, true, false, false, (float) Settings.WIDTH * 0.8F, (float) Settings.HEIGHT / 2.0F));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.6F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmallLaserEffectColored(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.hb.cX + EYE1_X, this.hb.cY + EYE1_Y, Color.GOLD), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(0), AbstractGameAction.AttackEffect.FIRE, false, true));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.7F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmallLaserEffectColored(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.hb.cX + EYE2_X, this.hb.cY + EYE2_Y, Color.GOLD), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(0), AbstractGameAction.AttackEffect.FIRE, false, true));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.8F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmallLaserEffectColored(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.hb.cX + EYE3_X, this.hb.cY + EYE3_Y, Color.GOLD), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(0), AbstractGameAction.AttackEffect.FIRE, false, true));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.YELLOW, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.GOLD, ShockWaveEffect.ShockWaveType.CHAOTIC), .5F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(1), AbstractGameAction.AttackEffect.SMASH));
                if (AbstractDungeon.cardRng.randomBoolean()) {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Pain(), 1, true, false));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Regret(), 1, true, false));
                }
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
                AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Shackled"));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strAmt * 3), this.strAmt * 3));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.blockAmt));
                break;
            case 4:
                this.halfDead = true;
                AbstractDungeon.actionManager.addToBottom(new NeowRezAction(this));
                break;
            case 5:
                break;
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {

        int roll = MathUtils.random(3);

        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEOW_1A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_NEOW_1B");
        } else if (roll == 2) {
            CardCrawlGame.sound.play("VO_NEOW_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEOW_2B");
        }

    }

    public void switchToRez() {
        if (!isRezzing) {
            this.setMove((byte) 4, Intent.MAGIC);
            isRezzing = true;
            createIntent();
        }


    }

    public void moveForRez() {
        if (offscreen) {
            this.heal(this.maxHealth);
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, NeowInvulnerablePower.POWER_ID, 1));

            movingBack = true;
            moveTimer = 2F;
            isRezzing = false;
            this.halfDead = false;
        } else {

            movingOffscreen = true;
            moveTimer = 2F;
        }

    }

    protected void getMove(int num) {
        if (minion != null || movingOffscreen || offscreen) {
            this.setMove((byte) 5, Intent.SLEEP);
        } else if (!isRezzing) {
            if (turnNum == 0) {
                this.setMove((byte) 0, Intent.STRONG_DEBUFF);
                turnNum = 1;
            } else {
                switch (turnNum) {
                    case 1:
                        this.setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base, 3, true);
                        break;
                    case 2:
                        this.setMove((byte) 2, Intent.ATTACK_DEBUFF, this.damage.get(1).base);
                        break;
                    case 3:
                        this.setMove((byte) 3, Intent.DEFEND_BUFF);
                        break;
                }

                this.turnNum++;
                if (this.turnNum == (4)) {
                    this.turnNum = 1;
                }
            }
        } else {
            this.setMove((byte) 4, Intent.MAGIC);
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            onBossVictoryLogic();
            onFinalBossVictoryLogic();
            CardCrawlGame.stopClock = true;

        }
    }
}



