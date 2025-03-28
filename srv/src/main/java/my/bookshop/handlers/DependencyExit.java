package my.bookshop.handlers;

import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.cds.Struct;
import com.sap.cds.sdm.model.Repository;
import com.sap.cds.sdm.service.RepoService;
import com.sap.cds.sdm.service.RepoServiceImpl;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cds.services.mt.*;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;

import java.util.*;
@Component
@ServiceName(DeploymentService.DEFAULT_NAME)
class DependencyExit implements EventHandler {
   
    @On(event = DeploymentService.EVENT_DEPENDENCIES)
    public void onGetDependencies(DependenciesEventContext context) {

        List<SaasRegistryDependency> dependencies = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, Object> uaa = (Map<String, Object>) getSDMCredentials().get("uaa");
        dependencies.add(SaasRegistryDependency.create(uaa.get("xsappname").toString()));
        context.setResult(dependencies);
    }
 private Map<String, Object> getSDMCredentials() {
    List<ServiceBinding> allServiceBindings =
    DefaultServiceBindingAccessor.getInstance().getServiceBindings();
// filter for a specific binding
ServiceBinding sdmBinding =
    allServiceBindings.stream()
        .filter(binding -> "sdm".equalsIgnoreCase(binding.getServiceName().orElse(null)))
        .findFirst()
        .get();
return  sdmBinding.getCredentials();

    }

    @After(event = DeploymentService.EVENT_SUBSCRIBE)
    public void onSubscribe(SubscribeEventContext context) throws JsonProcessingException,UnsupportedEncodingException {
    //use httpclient and onboard a repository
       System.out.println("After subscribing to my CAP application");
         final SaasRegistrySubscriptionOptions options = Struct
                    .access(context.getOptions())
                    .as(SaasRegistrySubscriptionOptions.class);

            // Access the specific property
            final String subdomain = options.getSubscribedSubdomain();
 System.out.println("subdomain "+subdomain);
RepoService repoService =  new RepoServiceImpl();
Repository repository = new Repository();
repository.setDescription("Onboarding Repo Demo");
repository.setDisplayName(" Test Onboarding repo");
repository.setExternalId(System.getenv("REPOSITORY_ID"));
repository.setSubdomain(subdomain);
repository.setTenantId(context.getTenant());
String response = repoService.onboardRepository(repository);
System.out.println("response"+response);
    }

    @After(event = DeploymentService.EVENT_UNSUBSCRIBE)
public void afterUnsubscribe(UnsubscribeEventContext context) {
    //delete onboarded repository
    System.out.println("After unsubscribing to my CAP application");
        final SaasRegistrySubscriptionOptions options = Struct
       .access(context.getOptions())
       .as(SaasRegistrySubscriptionOptions.class);
// Access the specific property
final String subdomain = options.getSubscribedSubdomain();
System.out.println("subdomain "+subdomain);

RepoService repoService =  new RepoServiceImpl();
String res = repoService.offboardRepository(subdomain);
System.out.println(res);
}
}