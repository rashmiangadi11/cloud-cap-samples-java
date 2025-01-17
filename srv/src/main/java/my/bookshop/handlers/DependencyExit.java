package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.ADDRESSES;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.stream.Stream;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cds.Result;
import com.sap.cds.Struct;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.sdm.model.Repository;
import com.sap.cds.sdm.model.SDMCredentials;
import com.sap.cds.sdm.service.RepoService;
import com.sap.cds.sdm.service.RepoServiceImpl;
import com.sap.cds.sdm.service.SDMService;
import com.sap.cds.sdm.service.SDMServiceImpl;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.authentication.AuthenticationInfo;
import com.sap.cds.services.authentication.JwtTokenAuthenticationInfo;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory.DefaultHttpClientFactoryBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2DestinationBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cds.services.mt.*;
import cds.gen.adminservice.Addresses;
import cds.gen.adminservice.Addresses_;
import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Orders;
import cds.gen.api_business_partner.ApiBusinessPartner;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.api_business_partner.BusinessPartnerChangedContext;
import my.bookshop.MessageKeys;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;

import java.util.*;
@Component
@ServiceName(DeploymentService.DEFAULT_NAME)
class DependencyExit implements EventHandler {
    private PersistenceService persistenceService;
    DependencyExit(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
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
//     ClientCredentials clientCredentials =
//           new ClientCredentials(
//               uaa.get("clientid").toString(), uaa.get("clientsecret").toString());
//               String baseTokenUrl = uaa.get("url").toString();
//               if (subdomain != null && !subdomain.equals("")) {
//                 String providersubdomain =
//                     baseTokenUrl.substring(baseTokenUrl.indexOf("/") + 2, baseTokenUrl.indexOf("."));
//                 baseTokenUrl = baseTokenUrl.replace(providersubdomain, subdomain);
//               }
//               var destination =
//           OAuth2DestinationBuilder.forTargetUrl(getSDMCredentials().get("uri").toString())
//               .withTokenEndpoint(baseTokenUrl)
//               .withClient(clientCredentials, OnBehalfOf.TECHNICAL_USER_PROVIDER)
//               .property("name", "sdm-token-fetch")
//               .build();

//       DefaultHttpClientFactoryBuilder builder = DefaultHttpClientFactory.builder();
//       builder.timeoutMilliseconds( 900000);
//       builder.maxConnectionsPerRoute(50);
//       builder.maxConnectionsTotal(50);
//       DefaultHttpClientFactory factory = builder.build();

//       HttpClient httpClient = factory.createHttpClient(destination);
//     String sdmUrl = getSDMCredentials().get("uri").toString() + "rest/v2/repositories";
//     System.out.println("sdmUrl "+ sdmUrl);

//     HttpPost onboardingReq = new HttpPost(sdmUrl);
//     ObjectMapper objectMapper = new ObjectMapper();
//     OnboardRepository onboardRepository =  new OnboardRepository();
//     Repository repository = new Repository();
//     repository.setDescription("Onboarding Repo Demo");
//     repository.setDisplayName(" Test Onboarding repo");
//     repository.setExternalId(System.getenv("REPOSITORY_ID"));
//     onboardRepository.setRepository(repository);
//     String json = objectMapper.writeValueAsString(onboardRepository);
//     StringEntity entity = new StringEntity(json);
//     onboardingReq.setEntity(entity);
//     // Set the content type of the request
//     onboardingReq.setHeader("Content-Type", "application/json");
//     try (var response = (CloseableHttpResponse) httpClient.execute(onboardingReq)) {
//         String responseString = EntityUtils.toString(response.getEntity());
//     //     System.out.println("Response "+ responseString+":"+response.getStatusLine().getStatusCode());
//     //     JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
//     //     String cmisRepositoryId = jsonObject.get("cmisRepositoryId").getAsString(); 
//     //    // context.getOptions().put("repositoryId",cmisRepositoryId);
//      CqnInsert insert = Insert.into("Repository").
// } catch (IOException e) {
//       throw new ServiceException("Error in onboarding ", e.getMessage());
//     }
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
// System.out.println("Repos Id "+context.getOptions().get("repositoryId"));
//        ClientCredentials clientCredentials =
//           new ClientCredentials(
//               uaa.get("clientid").toString(), uaa.get("clientsecret").toString());
//               String baseTokenUrl = uaa.get("url").toString();
//               if (subdomain != null && !subdomain.equals("")) {
//                 String providersubdomain =
//                     baseTokenUrl.substring(baseTokenUrl.indexOf("/") + 2, baseTokenUrl.indexOf("."));
//                 baseTokenUrl = baseTokenUrl.replace(providersubdomain, subdomain);
//               }
//               var destination =
//           OAuth2DestinationBuilder.forTargetUrl(getSDMCredentials().get("uri").toString())
//               .withTokenEndpoint(baseTokenUrl)
//               .withClient(clientCredentials, OnBehalfOf.TECHNICAL_USER_PROVIDER)
//               .property("name", "sdm-token-fetch")
//               .build();

//       DefaultHttpClientFactoryBuilder builder = DefaultHttpClientFactory.builder();
//       builder.timeoutMilliseconds( 900000);
//       builder.maxConnectionsPerRoute(50);
//       builder.maxConnectionsTotal(50);
//       DefaultHttpClientFactory factory = builder.build();

//       HttpClient httpClient = factory.createHttpClient(destination);
//     String sdmUrl = getSDMCredentials().get("uri").toString() + "rest/v2/repositories/"+context.getOptions().get("repositoryId");
//     System.out.println("sdmUrl "+ sdmUrl);

//     HttpDelete offboardingReq = new HttpDelete(sdmUrl);
    
//     // Set the content type of the request
//     offboardingReq.setHeader("Content-Type", "application/json");
//     try (var response = (CloseableHttpResponse) httpClient.execute(offboardingReq)) {
    
//      System.out.println("offboard "+ EntityUtils.toString(response.getEntity()));
//     } catch (IOException e) {
//       throw new ServiceException("Error in offboarding ", e.getMessage());
//     }
}
 public  String  getSubdomain(String token) {
    String[] chunks = token.split("\\.");
    java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
    String payload = new String(decoder.decode(chunks[1]));
    JsonElement jelement = new JsonParser().parse(payload);
    JsonObject payloadObj = jelement.getAsJsonObject();
   String email = payloadObj.get("email").getAsString();
   JsonObject tenantDetails = payloadObj.get("ext_attr").getAsJsonObject();
   return tenantDetails.get("zdn").getAsString();
  }
}