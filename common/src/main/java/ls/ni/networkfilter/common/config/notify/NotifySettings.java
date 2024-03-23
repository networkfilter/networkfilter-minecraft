package ls.ni.networkfilter.common.config.notify;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifySettings {

    @Valid
    @NotNull
    private DiscordNotifySettings discord;
}
