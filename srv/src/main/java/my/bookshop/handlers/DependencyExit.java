package my.bookshop.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.mt.DependenciesEventContext;
import com.sap.cds.services.mt.DeploymentService;
import com.sap.cds.services.mt.SaasRegistryDependency;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;

   @Component
@ServiceName(DeploymentService.DEFAULT_NAME)
class DependencyExit implements EventHandler {
 
    @On(event = DeploymentService.EVENT_DEPENDENCIES)
    public void onGetDependencies(DependenciesEventContext context) {
 
        List<SaasRegistryDependency> dependencies = new ArrayList<>();
 
        dependencies.add(SaasRegistryDependency.create(getSDMXsappName()));
        context.setResult(dependencies);
    }
 private String getSDMXsappName() {
    List<ServiceBinding> allServiceBindings =
    DefaultServiceBindingAccessor.getInstance().getServiceBindings();
// filter for a specific binding
ServiceBinding sdmBinding =
    allServiceBindings.stream()
        .filter(binding -> "sdm".equalsIgnoreCase(binding.getServiceName().orElse(null)))
        .findFirst()
        .get();
Map<String, Object> uaaCredentials = sdmBinding.getCredentials();
Map<String, Object> uaa = (Map<String, Object>) uaaCredentials.get("uaa");
 return uaa.get("xsappname").toString();
    }
} 

