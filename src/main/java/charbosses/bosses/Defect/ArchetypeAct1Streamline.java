package charbosses.bosses.Defect;

import charbosses.cards.blue.*;
import charbosses.relics.CBR_Abacus;
import charbosses.relics.CBR_DataDisk;
import charbosses.relics.CBR_NeowsBlessing;
import charbosses.relics.CBR_SmoothStone;
import charbosses.relics.EventRelics.CBR_Transmogrifier;

public class ArchetypeAct1Streamline extends ArchetypeBaseDefect {

    public ArchetypeAct1Streamline() {
        super("DF_ARCHETYPE_STREAMLINE", "Streamline");
    }

    public void initialize() {
        //Tuning Recommendation per Act (CARDS): 1 Card Removal, 2 Upgrades, ~6-8 cards added to deck
        //Tuning Recommendation per Act (RELICS): 2 relics and an Event relic (simulate what the Event did)
        //Tuning Recommendation for Act 2-3: At least 1 Rare and 1 Boss Relic in addition to above 2
        //Make the total cards always divisible by 3 - Shuffle should not occur on a partial hand

        //STARTER DECK - 4 Strikes, 4 Defends, 1 Zap, 1 Dualcast

        //1 Strike 1 Defend Removed

        //7 Cards Added, 3 Upgrades:
        //Cold Snap
        //Leap+
        //Streamline+
        //Ball Lightning+
        //Rebound
        //Charge Battery
        //Rip and Tear


        /////   RELICS   /////

        addRelic(new CBR_NeowsBlessing());

        //data disk
        addRelic(new CBR_SmoothStone());
        addRelic(new CBR_Abacus());
        addRelic(new CBR_Transmogrifier());  //Could be something else, no strong lean in any direction

        /////   CARDS   /////

        //Turn 1
        addToDeck(new EnZap(), false);
        addToDeck(new EnDefendBlue(), false);
        addToDeck(new EnDualcast(), false);

        //Turn 2
        addToDeck(new EnColdSnap(), false);
        addToDeck(new EnLeap(), true);
        addToDeck(new EnStrikeBlue(), false);

        //Turn 3
        addToDeck(new EnBallLightning(), true);
        addToDeck(new EnDefendBlue(), false);
        addToDeck(new EnDefendBlue(), false);

        //Turn 4
        addToDeck(new EnChargeBattery(), false);
        addToDeck(new EnRipAndTear(), false);
        addToDeck(new EnStrikeBlue(), false);

        //Turn 5
        addToDeck(new EnRebound(), false);
        addToDeck(new EnStreamline(), true);
        addToDeck(new EnStrikeBlue(), false);


    }

    @Override
    public void initializeBonusRelic() {
        addRelic(new CBR_Abacus());
    }
}