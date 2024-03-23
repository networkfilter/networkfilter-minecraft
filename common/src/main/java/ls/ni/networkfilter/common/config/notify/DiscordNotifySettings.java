package ls.ni.networkfilter.common.config.notify;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordNotifySettings {

    @NotNull
    private Boolean enabled;

    @NotNull
    private String webhook;

    @NotNull
    private String message;
}
