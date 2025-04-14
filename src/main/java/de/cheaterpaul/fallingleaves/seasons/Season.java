package de.cheaterpaul.fallingleaves.seasons;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public enum SubSeason {
        EARLY_SPRING(SPRING, SeasonAge.EARLY),
        MID_SPRING(SPRING, SeasonAge.MID),
        LATE_SPRING(SPRING, SeasonAge.LATE),
        EARLY_SUMMER(SUMMER, SeasonAge.EARLY),
        MID_SUMMER(SUMMER, SeasonAge.MID),
        LATE_SUMMER(SUMMER, SeasonAge.LATE),
        EARLY_AUTUMN(AUTUMN, SeasonAge.EARLY),
        MID_AUTUMN(AUTUMN, SeasonAge.MID),
        LATE_AUTUMN(AUTUMN, SeasonAge.LATE),
        EARLY_WINTER(WINTER, SeasonAge.EARLY),
        MID_WINTER(WINTER, SeasonAge.MID),
        LATE_WINTER(WINTER, SeasonAge.LATE);

        public final Season season;
        public final SeasonAge age;

        SubSeason(Season season, SeasonAge age) {
            this.season = season;
            this.age = age;
        }
    }

    public enum SeasonAge {
        EARLY,
        MID,
        LATE;
    }
}
