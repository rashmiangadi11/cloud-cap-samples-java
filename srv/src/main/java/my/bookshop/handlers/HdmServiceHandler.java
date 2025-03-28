package my.bookshop.handlers;

/*
import cds.gen.hdmservice.HarmonizedDocuments_;
import cds.gen.hdmservice.HDMRelations_;
import cds.gen.hdmservice.HdmService_;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import java.util.Collections;

import org.springframework.stereotype.Component;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.reflect.CdsService;

@Component
@ServiceName(HdmService_.CDS_NAME)
public class HdmServiceHandler implements EventHandler {

    @On(event = CqnService.EVENT_CREATE, entity = HarmonizedDocuments_.CDS_NAME)
    public void onCreateHarmonizedDocuments(CdsCreateEventContext context) {
        CdsReadEventContext context = genericContext.as(CdsReadEventContext.class);
        CqnSelect select = context.getCqn();
        context.setResult(Collections.emptyList());
        Result result = context.getResult();
    }

    @On(event = CqnService.EVENT_READ, entity = HarmonizedDocuments_.CDS_NAME)
    public void onReadHarmonizedDocuments(CdsReadEventContext context) {
        CqnSelect select = context.getCqn();
        context.setResult(context.getCdsService().run(select));
    }

    @On(event = CqnService.EVENT_UPDATE, entity = HarmonizedDocuments_.CDS_NAME)
    public void onUpdateHarmonizedDocuments(CdsUpdateEventContext context) {
        CqnUpdate update = context.getCqn();
        context.setResult(context.getCdsService().run(update));
    }

    @On(event = CqnService.EVENT_DELETE, entity = HarmonizedDocuments_.CDS_NAME)
    public void onDeleteHarmonizedDocuments(CdsDeleteEventContext context) {
        CqnDelete delete = context.getCqn();
        context.setResult(context.getCdsService().run(delete));
    }

    @On(event = CqnService.EVENT_CREATE, entity = HDMRelations_.CDS_NAME)
    public void onCreateHDMRelations(CdsCreateEventContext context) {
        // Perform the insert operation directly using the CQN build from the context
        context.setResult(context.getCdsService().run(context.getCqn()));
    }

    @On(event = CqnService.EVENT_READ, entity = HDMRelations_.CDS_NAME)
    public void onReadHDMRelations(CdsReadEventContext context) {
        CqnSelect select = context.getCqn();
        context.setResult(context.getCdsService().run(select));
    }

    @On(event = CqnService.EVENT_UPDATE, entity = HDMRelations_.CDS_NAME)
    public void onUpdateHDMRelations(CdsUpdateEventContext context) {
        CqnUpdate update = context.getCqn();
        context.setResult(context.getCdsService().run(update));
    }

    @On(event = CqnService.EVENT_DELETE, entity = HDMRelations_.CDS_NAME)
    public void onDeleteHDMRelations(CdsDeleteEventContext context) {
        CqnDelete delete = context.getCqn();
        context.setResult(context.getCdsService().run(delete));
    }
}
*/