package org.xuanfeng.idphotosbackend.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sightengine 图片安全检测接口返回结果 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SightengineCheckDTO {

    private String status;

    private RequestInfo request;

    private Nudity nudity;

    private Weapon weapon;

    @JSONField(name = "recreational_drug")
    private RecreationalDrug recreationalDrug;

    private Medical medical;

    private Alcohol alcohol;

    private Offensive offensive;

    private Text text;

    private List<Face> faces;

    @JSONField(name = "artificial_faces")
    private List<Object> artificialFaces;

    private Gore gore;

    private Qr qr;

    private Tobacco tobacco;

    private Violence violence;

    @JSONField(name = "self-harm")
    private SelfHarm selfHarm;

    private Gambling gambling;

    private Media media;

    // ========== 内部类 ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        private String id;
        private Double timestamp;
        private Integer operations;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Nudity {
        @JSONField(name = "sexual_activity")
        private Double sexualActivity;

        @JSONField(name = "sexual_display")
        private Double sexualDisplay;

        private Double erotica;

        @JSONField(name = "very_suggestive")
        private Double verySuggestive;

        private Double suggestive;

        @JSONField(name = "mildly_suggestive")
        private Double mildlySuggestive;

        @JSONField(name = "suggestive_classes")
        private SuggestiveClasses suggestiveClasses;

        private Double none;

        private Context context;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestiveClasses {
        private Double bikini;
        private Double cleavage;
        @JSONField(name = "cleavage_categories")
        private CleavageCategories cleavageCategories;
        private Double lingerie;
        @JSONField(name = "male_chest")
        private Double maleChest;
        @JSONField(name = "male_chest_categories")
        private MaleChestCategories maleChestCategories;
        @JSONField(name = "male_underwear")
        private Double maleUnderwear;
        private Double miniskirt;
        private Double minishort;
        @JSONField(name = "nudity_art")
        private Double nudityArt;
        private Double schematic;
        private Double sextoy;
        @JSONField(name = "suggestive_focus")
        private Double suggestiveFocus;
        @JSONField(name = "suggestive_pose")
        private Double suggestivePose;
        @JSONField(name = "swimwear_male")
        private Double swimwearMale;
        @JSONField(name = "swimwear_one_piece")
        private Double swimwearOnepiece;
        @JSONField(name = "visibly_undressed")
        private Double visiblyUndressed;
        private Double other;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CleavageCategories {
        @JSONField(name = "very_revealing")
        private Double veryRevealing;
        private Double revealing;
        private Double none;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaleChestCategories {
        @JSONField(name = "very_revealing")
        private Double veryRevealing;
        private Double revealing;
        @JSONField(name = "slightly_revealing")
        private Double slightlyRevealing;
        private Double none;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        @JSONField(name = "sea_lake_pool")
        private Double seaLakePool;
        @JSONField(name = "outdoor_other")
        private Double outdoorOther;
        @JSONField(name = "indoor_other")
        private Double indoorOther;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weapon {
        private WeaponClasses classes;
        @JSONField(name = "firearm_type")
        private FirearmType firearmType;
        @JSONField(name = "firearm_action")
        private FirearmAction firearmAction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeaponClasses {
        private Double firearm;
        @JSONField(name = "firearm_gesture")
        private Double firearmGesture;
        @JSONField(name = "firearm_toy")
        private Double firearmToy;
        private Double knife;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FirearmType {
        private Double animated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FirearmAction {
        @JSONField(name = "aiming_threat")
        private Double aimingThreat;
        @JSONField(name = "aiming_camera")
        private Double aimingCamera;
        @JSONField(name = "aiming_safe")
        private Double aimingSafe;
        @JSONField(name = "in_hand_not_aiming")
        private Double inHandNotAiming;
        @JSONField(name = "worn_not_in_hand")
        private Double wornNotInHand;
        @JSONField(name = "not_worn")
        private Double notWorn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecreationalDrug {
        private Double prob;
        private RecreationalDrugClasses classes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecreationalDrugClasses {
        private Double cannabis;
        @JSONField(name = "cannabis_logo_only")
        private Double cannabisLogoOnly;
        @JSONField(name = "cannabis_plant")
        private Double cannabisPlant;
        @JSONField(name = "cannabis_drug")
        private Double cannabisDrug;
        @JSONField(name = "recreational_drugs_not_cannabis")
        private Double recreationalDrugsNotCannabis;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Medical {
        private Double prob;
        private MedicalClasses classes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalClasses {
        private Double pills;
        private Double paraphernalia;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alcohol {
        private Double prob;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Offensive {
        private Double nazi;
        @JSONField(name = "asian_swastika")
        private Double asianSwastika;
        private Double confederate;
        private Double supremacist;
        private Double terrorist;
        @JSONField(name = "middle_finger")
        private Double middleFinger;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text {
        private List<Object> profanity;
        private List<Object> personal;
        private List<Object> link;
        private List<Object> social;
        private List<Object> extremism;
        private List<Object> medical;
        private List<Object> drug;
        private List<Object> weapon;
        @JSONField(name = "content-trade")
        private List<Object> contentTrade;
        @JSONField(name = "money-transaction")
        private List<Object> moneyTransaction;
        private List<Object> spam;
        private List<Object> violence;
        @JSONField(name = "self-harm")
        private List<Object> selfHarm;
        @JSONField(name = "has_artificial")
        private Double hasArtificial;
        @JSONField(name = "has_natural")
        private Double hasNatural;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Face {
        private Double x1;
        private Double y1;
        private Double x2;
        private Double y2;
        private FaceAttributes attributes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaceAttributes {
        private AgeInfo age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgeInfo {
        private Double minor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gore {
        private Double prob;
        private GoreClasses classes;
        private GoreType type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoreClasses {
        @JSONField(name = "very_bloody")
        private Double veryBloody;
        @JSONField(name = "slightly_bloody")
        private Double slightlyBloody;
        @JSONField(name = "body_organ")
        private Double bodyOrgan;
        @JSONField(name = "serious_injury")
        private Double seriousInjury;
        @JSONField(name = "superficial_injury")
        private Double superficialInjury;
        private Double corpse;
        private Double skull;
        private Double unconscious;
        @JSONField(name = "body_waste")
        private Double bodyWaste;
        private Double other;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoreType {
        private Double animated;
        private Double fake;
        private Double real;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Qr {
        private List<Object> personal;
        private List<Object> link;
        private List<Object> social;
        private List<Object> spam;
        private List<Object> profanity;
        private List<Object> blacklist;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tobacco {
        private Double prob;
        private TobaccoClasses classes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TobaccoClasses {
        @JSONField(name = "regular_tobacco")
        private Double regularTobacco;
        @JSONField(name = "ambiguous_tobacco")
        private Double ambiguousTobacco;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Violence {
        private Double prob;
        private ViolenceClasses classes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViolenceClasses {
        @JSONField(name = "physical_violence")
        private Double physicalViolence;
        @JSONField(name = "firearm_threat")
        private Double firearmThreat;
        @JSONField(name = "combat_sport")
        private Double combatSport;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelfHarm {
        private Double prob;
        private SelfHarmType type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelfHarmType {
        private Double real;
        private Double fake;
        private Double animated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gambling {
        private Double prob;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Media {
        private String id;
        private String uri;
    }
}
