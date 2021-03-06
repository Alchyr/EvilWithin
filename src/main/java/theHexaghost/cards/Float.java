package theHexaghost.cards;

import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import downfall.cards.OctoChoiceCard;
import expansioncontent.actions.OctoChoiceAction;
import expansioncontent.cards.*;
import expansioncontent.expansionContentMod;
import theHexaghost.GhostflameHelper;
import theHexaghost.HexaMod;
import theHexaghost.actions.AdvanceAction;
import theHexaghost.actions.OctoChoiceFloat;
import theHexaghost.actions.RetractAction;

import java.util.ArrayList;

public class Float extends AbstractHexaCard {

    public final static String ID = makeID("Float");

    //stupid intellij stuff SKILL, SELF, BASIC

    public Float() {
        super(ID, 0, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF);
        isEthereal = true;
    }

    public ArrayList<OctoChoiceCard> choiceList() {

        ArrayList<OctoChoiceCard> cardList = new ArrayList<>();
        cardList.add(new OctoChoiceCard("octo:OctoRetract", this.name, HexaMod.makeCardPath("Float.png"), this.EXTENDED_DESCRIPTION[1]));
        cardList.add(new OctoChoiceCard("octo:OctoNothing", this.name, HexaMod.makeCardPath("Float.png"), this.EXTENDED_DESCRIPTION[2]));
        cardList.add(new OctoChoiceCard("octo:OctoAdvance", this.name, HexaMod.makeCardPath("Float.png"), this.EXTENDED_DESCRIPTION[0]));

        return cardList;
    }

    public void doChoiceStuff(OctoChoiceCard card) {
        switch (card.cardID) {
            case "octo:OctoAdvance":
                atb(new AdvanceAction());
                break;
            case "octo:OctoRetract":
                atb(new RetractAction());
                break;
            case "octo:OctoNothing":
                break;
        }

    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AnimateHopAction(p));
        atb(new DrawCardAction(1));
        if (upgraded) {
            atb(new OctoChoiceFloat(this));
        } else {
            atb(new AdvanceAction());
        }


    }

    public void upgrade() {
        if (!upgraded) {
            upgradeName();

            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}