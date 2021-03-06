package sneckomod.cards.unknowns;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.function.Predicate;

public class UnknownRarePower extends AbstractUnknownCard {
    public final static String ID = makeID("UnknownRarePower");

    public UnknownRarePower() {
        super(ID, CardType.POWER, CardRarity.RARE);
    }

    @Override
    public Predicate<AbstractCard> myNeeds() {
        return c -> c.rarity == this.rarity && c.type == this.type;
    }
}
