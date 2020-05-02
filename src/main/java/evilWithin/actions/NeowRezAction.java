package evilWithin.actions;


import charbosses.bosses.AbstractCharBoss;
import charbosses.bosses.Defect.CharBossDefect;
import charbosses.bosses.Ironclad.CharBossIronclad;
import charbosses.bosses.Silent.CharBossSilent;
import charbosses.bosses.Watcher.CharBossWatcher;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import evilWithin.monsters.NeowBoss;
import evilWithin.vfx.NeowBossRezEffect;
import evilWithin.vfx.NeowRezFlareParticleEffect;
import evilWithin.vfx.ThrowGoldEffect;
import slimebound.SlimeboundMod;

import java.util.Collections;
import java.util.Iterator;

public class NeowRezAction extends AbstractGameAction {
    private NeowBoss owner;
    private boolean instructedMove;
    private boolean rezInit;
    public AbstractCharBoss cB;
    private NeowBossRezEffect rezVFX;

    public NeowRezAction(NeowBoss owner) {
        this.owner = owner;
        this.duration = 3F;
        this.instructedMove = false;
    }

    @Override
    public void update() {
        if (!this.instructedMove) {
            owner.moveForRez();
            this.instructedMove = true;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 1.5F && !rezInit) {
            this.rezInit = true;
            String name;
            if (owner.bossesToRez.size() == 0) {
                name = "EvilWithin:CharBossIronclad";
                SlimeboundMod.logger.info("WARNING: Neow had no bosses to rez.  Spawning an Ironclad by default.");
            } else {
                Collections.shuffle(owner.bossesToRez);
                name = owner.bossesToRez.get(0);
                owner.bossesToRez.remove(0);
            }
            SlimeboundMod.logger.info("Neow rezzing: " + name);
            rezBoss(name);
            SlimeboundMod.logger.info("Neow rezzed: " + cB.name);
            owner.minion = cB;
            cB.tint.color = new Color(.5F, .5F, 1F, 0F);
            cB.tint.changeColor(Color.WHITE.cpy(), 2F);
            AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.CYAN, true));
            AbstractDungeon.effectsQueue.add(new IntenseZoomEffect(cB.hb.cX, cB.hb.cY, false));
            rezVFX = new NeowBossRezEffect(cB.hb.cX, cB.hb.cY);
            AbstractDungeon.effectsQueue.add(rezVFX);

            AbstractDungeon.getCurrRoom().monsters.add(cB);

            if (ModHelper.isModEnabled("Lethality")) {
                this.addToBot(new ApplyPowerAction(cB, cB, new StrengthPower(cB, 3), 3));
            }

            if (ModHelper.isModEnabled("Time Dilation")) {
                this.addToBot(new ApplyPowerAction(cB, cB, new SlowPower(cB, 0)));
            }

            this.addToTop(new ApplyPowerAction(cB, cB, new MinionPower(cB)));

        }
        if (this.duration <= 0F) {
            cB.init();
            cB.showHealthBar();

            rezVFX.end();
            this.isDone = true;
        }
    }

    public void rezBoss(String name){
        //Separated here for patching in case of modded characters being made into bosses
        switch (name) {
            case "EvilWithin:CharBossIronclad": {
                cB = new CharBossIronclad();
                break;
            }
            case "EvilWithin:CharBossSilent": {
                cB = new CharBossSilent();
                break;
            }
            case "EvilWithin:CharBossDefect": {
                cB = new CharBossDefect();
                break;
            }
            case "EvilWithin:CharBossWatcher": {
                cB = new CharBossWatcher();
                break;
            }
            default: {
                cB = new CharBossIronclad();
                break;
            }
        }
    }
}