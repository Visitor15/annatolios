package com.voodootech.annatolios.fixtures;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestModelFixtures {

    public static class DerivedClassTwo extends CommonBase { }

    public static class DerivedClassOne extends CommonBase { }

    public static class CommonBase { }

    @JsonAutoDetect
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KhanAcademyBadge {

        private final String name;
        private final String description;
        private final String slug;

        private final Integer badge_category;
        private final Integer points;

        public KhanAcademyBadge(@JsonProperty("name")              final String name,
                                 @JsonProperty("description")       final String description,
                                 @JsonProperty("slug")              final String slug,
                                 @JsonProperty("badge_category")    final Integer badge_category,
                                 @JsonProperty("points")            final Integer points) {
            this.name           = name;
            this.description    = description;
            this.slug           = slug;
            this.badge_category = badge_category;
            this.points         = points;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getSlug() {
            return slug;
        }

        public Integer getBadge_category() {
            return badge_category;
        }

        public Integer getPoints() {
            return points;
        }
    }
}
