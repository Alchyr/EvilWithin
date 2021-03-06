package theHexaghost.cards;

import com.megacrit.cardcrawl.actions.common.UpgradeRandomCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theHexaghost.GhostflameHelper;
import theHexaghost.ghostflames.AbstractGhostflame;

public class InspiringFlames extends AbstractHexaCard {

    public final static String ID = makeID("InspiringFlames");

    //stupid intellij stuff SKILL, SELF, COMMON

    private static final int BLOCK = 5;
    private static final int UPG_BLOCK = 3;

    public InspiringFlames() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        baseBlock = BLOCK;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        for (AbstractGhostflame gf : GhostflameHelper.hexaGhostFlames) {
            if (gf.charged) {
                atb(new UpgradeRandomCardAction());
            }
        }
    }

    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPG_BLOCK);
        }
    }
}