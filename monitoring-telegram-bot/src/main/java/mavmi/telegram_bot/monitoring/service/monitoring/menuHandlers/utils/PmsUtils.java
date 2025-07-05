package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils;

import lombok.RequiredArgsConstructor;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PmsUtils {

    private final RemoteParameterPlugin parameterPlugin;

    public List<String> retrieveAllParamsNames() {
        return retrieveParametersNames().toList();
    }

    public List<String> retrieveFilteredParamsNames(String substring) {
        return retrieveParametersNames().filter(name -> name.toLowerCase().contains(substring.toLowerCase())).toList();
    }

    public Stream<String> retrieveParametersNames() {
        return retrieveAllParams()
                .stream()
                .map(Parameter::getName);
    }

    @Nullable
    public Parameter retrieveParameter(String name) {
        return parameterPlugin.getParameter(name);
    }

    public List<Parameter> retrieveAllParams() {
        return parameterPlugin.getAllParameters()
                        .stream()
                        .sorted(Comparator.comparing(Parameter::getName))
                        .toList();
    }
}
