package slimebound;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomUnlockBundle;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.exordium.GoopPuddle;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.SmokePuffEffect;
import downfall.downfallMod;
import expansioncontent.relics.StudyCardRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimebound.cards.*;
import slimebound.characters.SlimeboundCharacter;
import slimebound.dailymods.AllSplit;
import slimebound.events.*;
import slimebound.helpers.SelfDamageVariable;
import slimebound.helpers.SlimedVariable;
import slimebound.orbs.CultistSlime;
import slimebound.orbs.SpawnedSlime;
import slimebound.patches.AbstractCardEnum;
import slimebound.patches.SlimeboundEnum;
import slimebound.potions.SlimedPotion;
import slimebound.potions.SlimyTonguePotion;
import slimebound.potions.SpawnSlimePotion;
import slimebound.potions.ThreeZeroPotion;
import slimebound.powers.AcidTonguePowerUpgraded;
import slimebound.powers.DuplicatedFormNoHealPower;
import slimebound.relics.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static downfall.patches.EvilModeCharacterSelect.evilMode;


@com.evacipated.cardcrawl.modthespire.lib.SpireInitializer
public class SlimeboundMod implements OnCardUseSubscriber,
        SetUnlocksSubscriber,
        PostDungeonInitializeSubscriber, PostBattleSubscriber,
        PostInitializeSubscriber, PreMonsterTurnSubscriber,
        basemod.interfaces.EditCharactersSubscriber,
        basemod.interfaces.EditRelicsSubscriber,
        basemod.interfaces.EditCardsSubscriber,
        //basemod.interfaces.EditKeywordsSubscriber,
        //EditStringsSubscriber,
        //basemod.interfaces.PostDrawSubscriber,
        basemod.interfaces.OnStartBattleSubscriber {
    public static final boolean hasHubris;
    public static final String PROP_RELIC_SHARING = "contentSharing_relics";
    public static final String PROP_POTION_SHARING = "contentSharing_potions";
    public static final String PROP_EVENT_SHARING = "contentSharing_events";
    public static final String PROP_UNLOCK_ALL = "unlockEverything";
    public static final Logger logger = LogManager.getLogger(SlimeboundMod.class.getName());
    public static final com.badlogic.gdx.graphics.Color SLIME_COLOR = com.megacrit.cardcrawl.helpers.CardHelper.getColor(25.0F, 95.0F, 25.0F);
    private static final String SLIMEBOUNDMOD_ASSETS_FOLDER = "slimeboundResources/SlimeboundImages";
    private static final String ATTACK_CARD = "512/bg_attack_slimebound.png";
    private static final String SKILL_CARD = "512/bg_skill_slimebound.png";
    private static final String POWER_CARD = "512/bg_power_slimebound.png";
    private static final String ENERGY_ORB = "512/card_slimebound_orb.png";
    private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";
    private static final String ATTACK_CARD_PORTRAIT = "1024/bg_attack_slimebound.png";
    private static final String SKILL_CARD_PORTRAIT = "1024/bg_skill_slimebound.png";
    private static final String POWER_CARD_PORTRAIT = "1024/bg_power_slimebound.png";
    private static final String ENERGY_ORB_PORTRAIT = "1024/card_slimebound_orb.png";
    private static final String CHAR_BUTTON = "charSelect/button.png";
    private static final String CHAR_PORTRAIT = "charSelect/portrait.png";
    public static int attacksPlayedThisTurn;
    public static boolean slimeDelay;
    public static boolean huntedTriggered;
    public static boolean scrapping;
    public static SlimeboundCharacter slimeboundCharacter;
    public static boolean slimeTalked = false;
    public static boolean slimeTalkedAcidL = false;
    public static boolean slimeTalkedAcidM = false;
    public static boolean slimeTalkedAcidS = false;
    public static boolean slimeTalkedSpikeL = false;
    public static boolean slimeTalkedSpikeM = false;
    public static boolean slimeTalkedSpikeS = false;
    public static int slimeTalkedDark = 0;
    public static boolean slimeTalkedCollector = false;
    public static boolean spritealtered = false;
    public static boolean bumpnextlime = false;
    public static boolean disabledStrikeVFX = false;
    public static SpawnedSlime mostRecentSlime;
    public static boolean foughtSlimeBoss;

    @SpireEnum
    public static AbstractCard.CardTags LICK;

    @SpireEnum
    public static AbstractCard.CardTags TACKLE;
    public static Properties slimeboundDefault = new Properties();
    public static boolean contentSharing_relics = true;
    public static boolean contentSharing_potions = true;
    public static boolean contentSharing_events = true;
    public static boolean unlockEverything = false;
    public static ArrayList<AbstractRelic> shareableRelics = new ArrayList<>();
    public static boolean goopGlow = false;

    static {
        hasHubris = Loader.isModLoaded("Hubris");
        if (hasHubris) {
            logger.info("Detected Hubris");
        }
    }

    private ModPanel settingsPanel;
    private CustomUnlockBundle unlocks0;
    private CustomUnlockBundle unlocks1;
    private CustomUnlockBundle unlocks2;
    private CustomUnlockBundle unlocks3;
    private CustomUnlockBundle unlocks4;

    private static String modID;

    public SlimeboundMod() {


        BaseMod.subscribe(this);
        modID = "slimeboundmod";

        BaseMod.addColor(AbstractCardEnum.SLIMEBOUND,
                SLIME_COLOR, SLIME_COLOR, SLIME_COLOR, SLIME_COLOR, SLIME_COLOR, SLIME_COLOR, SLIME_COLOR,
                getResourcePath(ATTACK_CARD), getResourcePath(SKILL_CARD),
                getResourcePath(POWER_CARD), getResourcePath(ENERGY_ORB),
                getResourcePath(ATTACK_CARD_PORTRAIT), getResourcePath(SKILL_CARD_PORTRAIT),
                getResourcePath(POWER_CARD_PORTRAIT), getResourcePath(ENERGY_ORB_PORTRAIT), getResourcePath(CARD_ENERGY_ORB));

        slimeboundDefault.setProperty(PROP_EVENT_SHARING, "FALSE");
        slimeboundDefault.setProperty(PROP_RELIC_SHARING, "FALSE");
        slimeboundDefault.setProperty(PROP_POTION_SHARING, "FALSE");
        slimeboundDefault.setProperty(PROP_UNLOCK_ALL, "FALSE");

        loadConfigData();
    }

    public static String getResourcePath(String resource) {
        return "slimeboundResources/SlimeboundImages/" + resource;
    }

    public static void initialize() {
        new SlimeboundMod();
    }

    public static int getAcidTongueBonus(AbstractCreature source) {
        int bonus = 0;
        if (source != null) {
            if (source.hasPower(AcidTonguePowerUpgraded.POWER_ID)) {
                bonus = source.getPower(AcidTonguePowerUpgraded.POWER_ID).amount;
            }
        }
        return bonus;
    }

    public static void clearData() {
        saveData();
    }

    public static void saveData() {
        try {
            SpireConfig config = new SpireConfig("SlimeboundMod", "SlimeboundSaveData", slimeboundDefault);
            config.setBool(PROP_EVENT_SHARING, contentSharing_events);
            config.setBool(PROP_RELIC_SHARING, contentSharing_relics);
            config.setBool(PROP_POTION_SHARING, contentSharing_potions);
            config.setBool(PROP_UNLOCK_ALL, unlockEverything);

            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfigData() {
        try {
            logger.info("SlimeboundMod | Loading Config Preferences...");
            SpireConfig config = new SpireConfig("SlimeboundMod", "SlimeboundSaveData", slimeboundDefault);
            config.load();
            contentSharing_events = config.getBool(PROP_EVENT_SHARING);
            contentSharing_relics = config.getBool(PROP_RELIC_SHARING);
            contentSharing_potions = config.getBool(PROP_POTION_SHARING);
            unlockEverything = config.getBool(PROP_UNLOCK_ALL);
        } catch (Exception e) {
            e.printStackTrace();
            clearData();
        }
    }

    public static void triggerGoopCardVFX() {
        goopGlow = true;
    }

    public static String getModID() {
        return modID;
    }


    public static void checkForEndGoopCardVFX() {
        boolean noGoop = true;

        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if ((!monster.isDead) && (!monster.isDying)) {
                if (monster.hasPower(Slimed.ID)) {
                    noGoop = false;
                }
            }
        }

        if (noGoop) {
            goopGlow = false;
        }

    }

    public void receiveEditCharacters() {

        slimeboundCharacter = new SlimeboundCharacter("TheSlimebound", SlimeboundEnum.SLIMEBOUND);
        BaseMod.addCharacter(slimeboundCharacter, getResourcePath("charSelect/button.png"), getResourcePath("charSelect/portrait.png"), SlimeboundEnum.SLIMEBOUND, getResourcePath("charSelect/leaderboard.png"));

    }

    public void receivePostDungeonInitialize() {

        slimeTalked = false;
        slimeTalkedAcidL = false;
        slimeTalkedAcidM = false;
        slimeTalkedAcidS = false;
        slimeTalkedSpikeL = false;
        slimeTalkedSpikeM = false;
        slimeTalkedSpikeS = false;
        slimeTalkedDark = 0;
        slimeTalkedCollector = false;
        if (AbstractDungeon.player != null) {

                SlimeboundMod.foughtSlimeBoss = false;
                //SlimeboundMod.logger.info("Reset Hunted event bool.");

        }

    }

    public static AbstractOrb getLeadingSlime() {
        AbstractOrb oldestOrb = null;

        if (AbstractDungeon.player.maxOrbs > 0) {
            for (AbstractOrb o : AbstractDungeon.player.orbs) {
                return o;
            }
        }
        return oldestOrb;
    }

    @Override
    public void receiveSetUnlocks() {
        /*
        if (!unlockEverything) {
            unlocks0 = new CustomUnlockBundle(
                    DivideAndConquer.ID, ServeAndProtect.ID, CheckThePlaybook.ID
            );

            UnlockTracker.addCard(DivideAndConquer.ID);
            UnlockTracker.addCard(ServeAndProtect.ID);
            UnlockTracker.addCard(CheckThePlaybook.ID);

            unlocks1 = new CustomUnlockBundle(
                    SplitSpecialist.ID, TagTeam.ID, Darklings.ID
            );

            UnlockTracker.addCard(SplitSpecialist.ID);
            UnlockTracker.addCard(TagTeam.ID);
            UnlockTracker.addCard(Darklings.ID);

            unlocks2 = new CustomUnlockBundle(
                    HungryTackle.ID, Recollect.ID, Recycling.ID
            );

            UnlockTracker.addCard(HungryTackle.ID);
            UnlockTracker.addCard(Recollect.ID);
            UnlockTracker.addCard(Recycling.ID);


            unlocks3 = new CustomUnlockBundle(AbstractUnlock.UnlockType.RELIC,
                    SlimedTailRelic.ID, PotencyRelic.ID, SlimedSkullRelic.ID
            );

            UnlockTracker.addRelic(SlimedTailRelic.ID);
            UnlockTracker.addRelic(PotencyRelic.ID);
            UnlockTracker.addRelic(SlimedSkullRelic.ID);

            unlocks4 = new CustomUnlockBundle(AbstractUnlock.UnlockType.RELIC,
                    PreparedRelic.ID, StudyCardRelic.ID, StickyStick.ID
            );

            UnlockTracker.addRelic(PreparedRelic.ID);
            UnlockTracker.addRelic(StudyCardRelic.ID);
            UnlockTracker.addRelic(StickyStick.ID);


            BaseMod.addUnlockBundle(unlocks0, SlimeboundEnum.SLIMEBOUND, 0);

            BaseMod.addUnlockBundle(unlocks1, SlimeboundEnum.SLIMEBOUND, 1);

            BaseMod.addUnlockBundle(unlocks2, SlimeboundEnum.SLIMEBOUND, 2);

            BaseMod.addUnlockBundle(unlocks3, SlimeboundEnum.SLIMEBOUND, 3);

            BaseMod.addUnlockBundle(unlocks4, SlimeboundEnum.SLIMEBOUND, 4);

        }

*/
    }

    public void clearUnlockBundles() {
        BaseMod.removeUnlockBundle(SlimeboundEnum.SLIMEBOUND, 0);
        BaseMod.removeUnlockBundle(SlimeboundEnum.SLIMEBOUND, 1);
        BaseMod.removeUnlockBundle(SlimeboundEnum.SLIMEBOUND, 2);
        BaseMod.removeUnlockBundle(SlimeboundEnum.SLIMEBOUND, 3);
        BaseMod.removeUnlockBundle(SlimeboundEnum.SLIMEBOUND, 4);
        receiveSetUnlocks();
    }

    public void printEnemies() {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            logger.info(monster.name + " HP " + monster.currentHealth);
        }
    }

    public void receiveEditRelics() {
        BaseMod.addRelicToCustomPool(new AbsorbEndCombat(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new AbsorbEndCombatUpgraded(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new AggressiveSlimeRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new MaxSlimesRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new PotencyRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new SlimedTailRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new SlimedSkullRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new ScrapOozeRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new GreedOozeRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new SelfDamagePreventRelic(), AbstractCardEnum.SLIMEBOUND);
        BaseMod.addRelicToCustomPool(new TarBlob(), AbstractCardEnum.SLIMEBOUND);


    }

    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new SelfDamageVariable());
        BaseMod.addDynamicVariable(new SlimedVariable());

        BaseMod.addCard(new DivideAndConquerDivide());
        BaseMod.addCard(new DivideAndConquerConquer());
        BaseMod.addCard(new DivideAndConquer());

        BaseMod.addCard(new ServeAndProtectProtect());
        BaseMod.addCard(new ServeAndProtectServe());
        BaseMod.addCard(new ServeAndProtect());

        BaseMod.addCard(new slimebound.cards.Defend_Slimebound());
        BaseMod.addCard(new slimebound.cards.Strike_Slimebound());
        BaseMod.addCard(new BronzeBeam());
        BaseMod.addCard(new LevelUp());
        BaseMod.addCard(new SplitBruiser());
        BaseMod.addCard(new SplitAcid());
        BaseMod.addCard(new SplitLeeching());
        BaseMod.addCard(new SplitLicking());
        BaseMod.addCard(new ProtectTheBoss());
        //BaseMod.addCard(new slimebound.cards.zzzAbsorbAll());
        BaseMod.addCard(new Overexert());
        BaseMod.addCard(new Split());
        //BaseMod.addCard(new SuperSplit());
        BaseMod.addCard(new LeadByExample());
        BaseMod.addCard(new Nibble());
        BaseMod.addCard(new slimebound.cards.SlimeTap());
        BaseMod.addCard(new Teamwork());
        BaseMod.addCard(new slimebound.cards.SlimeBarrage());
        BaseMod.addCard(new SlimeBrawl());
        //BaseMod.addCard(new slimebound.cards.zzzMaxSlimes());
        BaseMod.addCard(new SlimeSpikes());

        BaseMod.addCard(new GoopArmor());
        BaseMod.addCard(new MassRepurpose());
        BaseMod.addCard(new DouseInSlime());
        BaseMod.addCard(new Chomp());
        BaseMod.addCard(new BestDefense());
        BaseMod.addCard(new OozeBath());
        //BaseMod.addCard(new zzzSoTasty());
        BaseMod.addCard(new LivingWall());
        BaseMod.addCard(new MinionMaster());
        BaseMod.addCard(new SelfFormingGoo());
        BaseMod.addCard(new slimebound.cards.Dissolve());
        BaseMod.addCard(new slimebound.cards.DuplicatedForm());
        BaseMod.addCard(new slimebound.cards.LeechingTouch());
        BaseMod.addCard(new SamplingLick());
        BaseMod.addCard(new FormOfPuddle());
        BaseMod.addCard(new slimebound.cards.Lick());
        BaseMod.addCard(new slimebound.cards.MegaLick());

        BaseMod.addCard(new PressTheAttack());
        //BaseMod.addCard(new SoulSicken());
        // BaseMod.addCard(new slimebound.cards.zzzFocusedLick());
        BaseMod.addCard(new HauntingLick());
        //BaseMod.addCard(new AcidGelatin());
        BaseMod.addCard(new RejuvenatingLick());
        BaseMod.addCard(new slimebound.cards.TongueLash());
        BaseMod.addCard(new ItLooksTasty());
        BaseMod.addCard(new slimebound.cards.AcidTongue());
        //BaseMod.addCard(new slimebound.cards.TendrilStrike());
        //BaseMod.addCard(new slimebound.cards.PoisonLick());
        BaseMod.addCard(new slimebound.cards.WasteNot());
        BaseMod.addCard(new HungryTackle());
        BaseMod.addCard(new slimebound.cards.FlameTackle());
        BaseMod.addCard(new RollThrough());
        BaseMod.addCard(new ComboTackle());
        BaseMod.addCard(new GoopTackle());
        //BaseMod.addCard(new VenomTackle());
        BaseMod.addCard(new slimebound.cards.Grow());
        BaseMod.addCard(new slimebound.cards.Prepare());
        BaseMod.addCard(new slimebound.cards.Gluttony());
        //BaseMod.addCard(new slimebound.cards.UsefulSlime());
        BaseMod.addCard(new RainOfGoop());
        BaseMod.addCard(new slimebound.cards.GoopSpray());
        BaseMod.addCard(new slimebound.cards.MassFeed());
        BaseMod.addCard(new ViciousTackle());
        BaseMod.addCard(new slimebound.cards.LeechEnergy());
        BaseMod.addCard(new LeechLife());
        BaseMod.addCard(new Equalize());

        BaseMod.addCard(new DisruptingSlam());
        BaseMod.addCard(new slimebound.cards.CorrosiveSpit());
        BaseMod.addCard(new PrepareCrush());
        BaseMod.addCard(new slimebound.cards.SlimeCrush());

        BaseMod.addCard(new slimebound.cards.Tackle());
        //BaseMod.addCard(new zzzSlimepotheosis());
        BaseMod.addCard(new slimebound.cards.FinishingTackle());
        BaseMod.addCard(new FeelOurPain());
        BaseMod.addCard(new Replication());
        BaseMod.addCard(new CheckThePlaybook());
        BaseMod.addCard(new Repurpose());
        BaseMod.addCard(new GrowthPunch());
        BaseMod.addCard(new slimebound.cards.Recycling());
        BaseMod.addCard(new slimebound.cards.Recollect());
        BaseMod.addCard(new slimebound.cards.Icky());

        BaseMod.addCard(new SplitSpecialist());
        BaseMod.addCard(new Darklings());
        BaseMod.addCard(new Schlurp());
        BaseMod.addCard(new SlimeSlap());
        BaseMod.addCard(new OneTwoCombo());
        BaseMod.addCard(new ForwardTackle());
        BaseMod.addCard(new TagTeam());
        BaseMod.addCard(new RallyTheTroops());


    }

    public void unlockEverything() {

        UnlockTracker.unlockCard(Strike_Slimebound.ID);
        UnlockTracker.unlockCard(Defend_Slimebound.ID);
        UnlockTracker.unlockCard(BronzeBeam.ID);
        UnlockTracker.unlockCard(LevelUp.ID);
        UnlockTracker.unlockCard(Tackle.ID);
        UnlockTracker.unlockCard(Schlurp.ID);
        UnlockTracker.unlockCard(Icky.ID);
        UnlockTracker.unlockCard(SplitBruiser.ID);
        UnlockTracker.unlockCard(SplitLeeching.ID);
        UnlockTracker.unlockCard(SplitAcid.ID);
        UnlockTracker.unlockCard(SplitLicking.ID);
        UnlockTracker.unlockCard(ProtectTheBoss.ID);
        //UnlockTracker.unlockCard(zzzAbsorbAll.ID);
        UnlockTracker.unlockCard(Overexert.ID);
        UnlockTracker.unlockCard(Split.ID);
        //UnlockTracker.unlockCard(SuperSplit.ID);
        UnlockTracker.unlockCard(LeadByExample.ID);
        UnlockTracker.unlockCard(SlimeTap.ID);
        UnlockTracker.unlockCard(Nibble.ID);
        UnlockTracker.unlockCard(RainOfGoop.ID);
        UnlockTracker.unlockCard(Teamwork.ID);
        UnlockTracker.unlockCard(SlimeBarrage.ID);
        UnlockTracker.unlockCard(SlimeBrawl.ID);
        //UnlockTracker.unlockCard(zzzMaxSlimes.ID);
        UnlockTracker.unlockCard(SelfFormingGoo.ID);
        UnlockTracker.unlockCard(SlimeSpikes.ID);
        UnlockTracker.unlockCard(GoopArmor.ID);
        UnlockTracker.unlockCard(MassRepurpose.ID);
        UnlockTracker.unlockCard(DouseInSlime.ID);
        UnlockTracker.unlockCard(Chomp.ID);
        UnlockTracker.unlockCard(BestDefense.ID);
        UnlockTracker.unlockCard(OozeBath.ID);
        UnlockTracker.unlockCard(MinionMaster.ID);
        // UnlockTracker.unlockCard(zzzSoTasty.ID);
        UnlockTracker.unlockCard(LivingWall.ID);

        UnlockTracker.unlockCard(LeechingTouch.ID);
        UnlockTracker.unlockCard(DuplicatedForm.ID);
        UnlockTracker.unlockCard(FeelOurPain.ID);
        UnlockTracker.unlockCard(Dissolve.ID);
        UnlockTracker.unlockCard(RollThrough.ID);
        UnlockTracker.unlockCard(CorrosiveSpit.ID);
        UnlockTracker.unlockCard(SamplingLick.ID);
        UnlockTracker.unlockCard(Recycling.ID);
        UnlockTracker.unlockCard(GrowthPunch.ID);
        UnlockTracker.unlockCard(Recollect.ID);
        UnlockTracker.unlockCard(FormOfPuddle.ID);
        UnlockTracker.unlockCard(Gluttony.ID);
        UnlockTracker.unlockCard(Lick.ID);
        UnlockTracker.unlockCard(MegaLick.ID);

        UnlockTracker.unlockCard(PressTheAttack.ID);
        //UnlockTracker.unlockCard(SoulSicken.ID);
        // UnlockTracker.unlockCard(zzzFocusedLick.ID);
        UnlockTracker.unlockCard(HauntingLick.ID);
        //UnlockTracker.unlockCard(AcidGelatin.ID);
        UnlockTracker.unlockCard(RejuvenatingLick.ID);
        UnlockTracker.unlockCard(Replication.ID);

        UnlockTracker.unlockCard(CheckThePlaybook.ID);
        UnlockTracker.unlockCard(FinishingTackle.ID);
        //UnlockTracker.unlockCard(zzzSlimepotheosis.ID);
        UnlockTracker.unlockCard(TongueLash.ID);
        //UnlockTracker.unlockCard(PoisonLick.ID);
        UnlockTracker.unlockCard(ItLooksTasty.ID);
        UnlockTracker.unlockCard(AcidTongue.ID);
        //UnlockTracker.unlockCard(TendrilStrike.ID);
        UnlockTracker.unlockCard(WasteNot.ID);
        UnlockTracker.unlockCard(HungryTackle.ID);
        //UnlockTracker.unlockCard(VenomTackle.ID);
        UnlockTracker.unlockCard(GoopTackle.ID);
        UnlockTracker.unlockCard(FlameTackle.ID);
        UnlockTracker.unlockCard(GoopSpray.ID);
        UnlockTracker.unlockCard(ComboTackle.ID);
        UnlockTracker.unlockCard(Grow.ID);
        UnlockTracker.unlockCard(Prepare.ID);
        UnlockTracker.unlockCard(MassFeed.ID);
        UnlockTracker.unlockCard(ViciousTackle.ID);
        UnlockTracker.unlockCard(LeechEnergy.ID);
        UnlockTracker.unlockCard(LeechLife.ID);
        UnlockTracker.unlockCard(Equalize.ID);

        UnlockTracker.unlockCard(DisruptingSlam.ID);
        UnlockTracker.unlockCard(PrepareCrush.ID);
        UnlockTracker.unlockCard(Repurpose.ID);

        UnlockTracker.unlockCard(ServeAndProtectProtect.ID);
        UnlockTracker.unlockCard(ServeAndProtect.ID);
        UnlockTracker.unlockCard(ServeAndProtectServe.ID);
        UnlockTracker.unlockCard(DivideAndConquerDivide.ID);

        UnlockTracker.unlockCard(DivideAndConquerConquer.ID);
        UnlockTracker.unlockCard(DivideAndConquer.ID);

        UnlockTracker.unlockCard(SplitSpecialist.ID);
        UnlockTracker.unlockCard(Darklings.ID);
        UnlockTracker.unlockCard(SlimeSlap.ID);
        UnlockTracker.unlockCard(OneTwoCombo.ID);
        UnlockTracker.unlockCard(ForwardTackle.ID);
        UnlockTracker.unlockCard(TagTeam.ID);
        UnlockTracker.unlockCard(RallyTheTroops.ID);

        //UnlockTracker.addScore(SlimeboundEnum.SLIMEBOUND, 1000000);

        clearUnlockBundles();


    }

    public void receivePostBattle(AbstractRoom r) {

        goopGlow = false;

        AbstractPlayer p = AbstractDungeon.player;
        if (spritealtered) {
            AbstractDungeon.effectsQueue.add(new SmokePuffEffect(p.hb.cX, p.hb.cY));
            // AbstractDungeon.actionManager.addToBottom(new VFXAction(new DoubleSlimeParticle(AbstractDungeon.player)));
            if (p instanceof SlimeboundCharacter) {
                SlimeboundCharacter hero = (SlimeboundCharacter) p;
                hero.setRenderscale(1F);
            }
            p.hb_x = p.hb_x - (100 * Settings.scale);
            p.drawX = p.drawX + (100 * Settings.scale);
            p.hb.cX = p.hb.cX - (100 * Settings.scale);


            spritealtered = false;
        }

        ArrayList<AbstractOrb> slimes = new ArrayList<>();

        for (AbstractOrb o : AbstractDungeon.player.orbs) {
            if (o instanceof CultistSlime) {
                ((CultistSlime) o).cleanUpVFX();
            }
            if (o instanceof SpawnedSlime) {
                slimes.add(o);
            }
        }
        boolean soundPlayed = false;
        for (AbstractOrb slime : slimes) {
            SpawnedSlime s = (SpawnedSlime) slime;
            s.noEvokeBonus = true;
            if (soundPlayed) {
                s.noEvokeSound = true;
            } else {
                soundPlayed = true;
            }
            s.triggerEvokeAnimation();
            if (AbstractDungeon.player.hasPower(DuplicatedFormNoHealPower.POWER_ID)) {
                AbstractDungeon.player.getPower(DuplicatedFormNoHealPower.POWER_ID).onVictory();
            }

            s.noRender = true;
        }
    }

    public void receivePostInitialize() {
        addPotions();

        //Dungeon patch contains the content sharing event logic

        /*BaseMod.addEvent(Hunted.ID, Hunted.class, TheCity.ID);
        BaseMod.addEvent(Hunted.ID, Hunted.class, TheBeyond.ID);
        BaseMod.addEvent(ArtOfSlimeWar.ID, ArtOfSlimeWar.class, TheCity.ID);*/

        BaseMod.addEvent(new AddEventParams.Builder(Hunted.ID, Hunted.class) //Event ID//
                //Event Character//
                .playerClass(SlimeboundEnum.SLIMEBOUND)
                //Act//
                //Only in Evil if content sharing is disabled
                .spawnCondition(() -> (downfallMod.contentSharing_events && downfallMod.contentSharing_colorlessCards))

                .dungeonIDs(TheCity.ID, TheBeyond.ID, "TheJungle")
                //Additional Condition//
                .bonusCondition(() -> (!foughtSlimeBoss || AbstractDungeon.player.hasRelic(StudyCardRelic.ID)))
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(ArtOfSlimeWar.ID, ArtOfSlimeWar.class) //Event ID//
                //Act//
                .dungeonIDs(TheCity.ID, "TheJungle")
                //Only in Evil if content sharing is disabled
                .spawnCondition(() -> (evilMode || downfallMod.contentSharing_events))
                .create());
        BaseMod.addEvent(new AddEventParams.Builder(WorldOfGoopSlimebound.ID, WorldOfGoopSlimebound.class) //Event ID//
                //Event Character//
                .playerClass(SlimeboundEnum.SLIMEBOUND)
                //Existing Event to Override//
                .overrideEvent(GoopPuddle.ID)
                //Additional Condition//
                .bonusCondition(() -> !AbstractDungeon.player.hasRelic(GreedOozeRelic.ID))
                //Event Type//
                .eventType(EventUtils.EventType.FULL_REPLACE)
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(ScrapOozeSlimebound.ID, ScrapOozeSlimebound.class) //Event ID//
                //Event Character//
                .playerClass(SlimeboundEnum.SLIMEBOUND)
                //Existing Event to Override//
                .overrideEvent(ScrapOoze.ID)
                //Additional Condition//
                .bonusCondition(() -> !AbstractDungeon.player.hasRelic(ScrapOozeRelic.ID))
                //Event Type//
                .eventType(EventUtils.EventType.FULL_REPLACE)
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(DarklingsSlimebound.ID, DarklingsSlimebound.class) //Event ID//
                //Act//
                .dungeonIDs(TheBeyond.ID)
                //Additional Condition//
                .bonusCondition(() -> (AbstractDungeon.player instanceof SlimeboundCharacter))
                .create());

        /*
        if (Loader.isModLoaded("TheJungle")){
            BaseMod.addEvent(Hunted.ID, Hunted.class, Jungle.ID);
            BaseMod.addEvent(ArtOfSlimeWar.ID, ArtOfSlimeWar.class, Jungle.ID);
        }
        */

        //BaseMod.addEvent(ArtOfSlimeWar.ID, ArtOfSlimeWar.class, Exordium.ID);


        //BaseMod.addEvent(WorldOfGoopSlimebound.ID, WorldOfGoopSlimebound.class, Exordium.ID);

    }

    public void refreshPotions() {
        BaseMod.removePotion(ThreeZeroPotion.POTION_ID);
        BaseMod.removePotion(SlimedPotion.POTION_ID);
        BaseMod.removePotion(SpawnSlimePotion.POTION_ID);
        BaseMod.removePotion(SlimyTonguePotion.POTION_ID);

        addPotions();
    }

    public void addPotions() {

        BaseMod.addPotion(ThreeZeroPotion.class, Color.FOREST, Color.BLACK, Color.BLACK, ThreeZeroPotion.POTION_ID);
        BaseMod.addPotion(SlimedPotion.class, Color.PURPLE, Color.PURPLE, Color.MAROON, SlimedPotion.POTION_ID, SlimeboundEnum.SLIMEBOUND);
        BaseMod.addPotion(SpawnSlimePotion.class, Color.GREEN, Color.FOREST, Color.BLACK, SpawnSlimePotion.POTION_ID, SlimeboundEnum.SLIMEBOUND);
        BaseMod.addPotion(SlimyTonguePotion.class, Color.PURPLE, Color.PURPLE, Color.MAROON, SlimyTonguePotion.POTION_ID, SlimeboundEnum.SLIMEBOUND);

    }


    public void receiveCardUsed(AbstractCard c) {

        if (c.type == AbstractCard.CardType.ATTACK) {
            ++attacksPlayedThisTurn;
        }

    }


    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        slimeDelay = true;
        attacksPlayedThisTurn = 0;
        //   this.printEnemies();

        return true;
    }

    public void receiveOnBattleStart(AbstractRoom room) {
        attacksPlayedThisTurn = 0;
    }

}
