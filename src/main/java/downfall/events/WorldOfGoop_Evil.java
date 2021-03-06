package downfall.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import slimebound.cards.Icky;

public class WorldOfGoop_Evil extends AbstractImageEvent {
    public static final String ID = "downfall:WorldOfGoop";
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final EventStrings eventStrings;
    private static final String DIALOG_1;
    private static final String GOLD_DIALOG;
    private static final String LEAVE_DIALOG;

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        GOLD_DIALOG = DESCRIPTIONS[1];
        LEAVE_DIALOG = DESCRIPTIONS[2];
    }

    private CurScreen screen;
    private int gold;

    public WorldOfGoop_Evil() {
        super(NAME, DIALOG_1, "images/events/goopPuddle.jpg");
        this.screen = CurScreen.INTRO;

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.gold = 150;
        } else {
            this.gold = 200;
        }

        this.imageEventText.setDialogOption(OPTIONS[0] + this.gold + OPTIONS[1], new Icky());
        this.imageEventText.setDialogOption(OPTIONS[2], new Icky());
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SPIRITS");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(GOLD_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.screen = CurScreen.RESULT;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Icky(), (float) Settings.WIDTH * .75F + 10.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Icky(), (float) Settings.WIDTH * .25F + 10.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.gold));
                        AbstractDungeon.player.gainGold(this.gold);
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(LEAVE_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.screen = CurScreen.RESULT;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Icky(), (float) Settings.WIDTH * .5F + 10.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();
        }
    }

    private enum CurScreen {
        INTRO,
        RESULT;

        CurScreen() {
        }
    }
}
