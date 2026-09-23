package net.wantedvisuals; // Если имя вашей папки отличается от net/wantedvisuals, замените этот путь

public class Module {
    private final String name;
    private final String description;
    private final String category;
    private boolean enabled;
    private int bind;

    public Module(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.bind = 0;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void toggle() { this.enabled = !this.enabled; }
    public int getBind() { return bind; }
    public void setBind(int bind) { this.bind = bind; }
}
