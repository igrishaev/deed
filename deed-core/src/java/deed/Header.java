package deed;

public record Header(short version) {
    public static Header of(short version) {
        return new Header(version);
    }
}
