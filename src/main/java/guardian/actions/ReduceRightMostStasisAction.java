//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package guardian.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import guardian.GuardianMod;
import guardian.orbs.StasisOrb;
import guardian.powers.AutomayhemPower;

public class ReduceRightMostStasisAction extends AbstractGameAction {

    private boolean fromRelic;

    public ReduceRightMostStasisAction(boolean fromRelic) {
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = AttackEffect.SLASH_HORIZONTAL;
        this.duration = 0.01F;
        this.fromRelic = fromRelic;

    }

    public void update() {
        if (AbstractDungeon.player.orbs.size() > 0) {
            GuardianMod.logger.info("ReduceRightMostStasis firing.");
            for (AbstractOrb o : AbstractDungeon.player.orbs) {
                if (o instanceof StasisOrb) {
                    if ((!this.fromRelic || (this.fromRelic && ((StasisOrb) o).passiveAmount > 1))) {
                        o.onStartOfTurn();
                        if (this.fromRelic) AbstractDungeon.player.getPower(AutomayhemPower.POWER_ID).flash();
                        break;
                    }
                }
            }


        }

        this.isDone = true;
    }

}
