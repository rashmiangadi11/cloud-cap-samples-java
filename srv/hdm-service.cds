using {my.bookshop as my} from '../db/index';
using {sap.changelog as changelog} from 'com.sap.cds/change-tracking';

@path : 'hdm'
service HdmService {

  entity HarmonizedDocuments as projection on my.HarmonizedDocuments;
  entity HDMRelations as projection on my.HDMRelations;

  entity TypeObject as projection on my.TypeObject;
  entity TypeProperty as projection on my.TypeProperty;
  entity TypeObjectData as projection on my.TypeObjectData;
  entity TypePropertyData as projection on my.TypePropertyData;


    // access control restrictions
    annotate HdmService with @restrict : [
        {
            grant : '*',
            to : 'authenticated-user',
        },
        {
            grant : '*',
            to : 'admin',
        }
    ];
}



// Deep Search Items
annotate HdmService.HDMRelations with @cds.search : {
  uniqueObjectKey,
  objectType,
  documentId,
  baseDocType,
  harmonizedDocument
};

annotate HdmService.HarmonizedDocuments with @cds.search : {
  documentId,
  baseDocType,
  cmisTypeObjectID,
  hdmRelations,
  typeObject,
  typeObjectData,
};

annotate HdmService.TypeObject with @cds.search : {
  typeID,
  baseID,
  typeProperty,
  typeObjectData,
  harmonizedDocument  
};

annotate HdmService.TypeProperty with @cds.search : {
  typeObjectID,
  typePropertyID,
  typePropertyData,
  typeObject
};

annotate HdmService.TypeObjectData with @cds.search : {
  typeObjectID,
  documentId,
  typeObject,
  harmonizedDocument,
  typePropertyData
};

annotate HdmService.TypeObjectData with @cds.search : {
  typeObjectDataID,
  typePropertyID,
  propertyValue,
  typeObjectData,
  typeProperty
};