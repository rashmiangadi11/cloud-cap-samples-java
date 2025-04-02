package my.bookshop.handlers;

import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.cds.Struct;
import com.sap.cds.sdm.model.Repository;
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
SDMAdminService sdmAdminService =  new SDMAdminServiceImpl();

Repository repository = new Repository();
repository.setDescription("Onboarding Repo Demo");
repository.setDisplayName(" Test Onboarding repo");
repository.setExternalId(System.getenv("REPOSITORY_ID"));
repository.setSubdomain(subdomain);
repository.setHashAlgorithms("SHA-256");
repository.setIsEncryptionEnabled(false);
repository.setIsVirusScanEnabled(true);
List<RepositoryParams> repositoryParams = new ArrayList<>();
RepositoryParams repositoryParam = new RepositoryParams();
      repositoryParam.setParamName("fileExtensions");
      JsonObject fileExtensionsValue = new JsonObject();
      fileExtensionsValue.addProperty("type", "block");
      fileExtensionsValue.add(
          "list", new Gson().toJsonTree(new String[] {"docx","pptx"}));

      // Convert the nested JSON object to a JSON string
      String jsonParamValue = fileExtensionsValue.toString();
      repositoryParam.setParamValue(jsonParamValue);
      repositoryParams.add(repositoryParam);
      repository.setRepositoryParams(repositoryParams);
      System.out.println("Repo Param "+repositoryParams);
      System.out.println("Repo  "+repository);
String response = sdmAdminService.onboardRepository(repository);
System.out.println("Onboard response "+response);
    }

}
