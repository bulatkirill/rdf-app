const fs = require('fs');
const RDFBuilder = require('./RDFBuilder');
const KB_RESOURCE_TRIDODPAD = 'kbrto';
const KB_LOOKUPS_TRASH_TYPE_NAME = 'kblttn';
const KB_LOOKUPS_CONTAINER_TYPE = 'kblct';
const KB_RESOURCE_TRASH_TYPE_NAME = 'kbrttn';
const KB_RESOURCE_CONTAINER_TYPE = 'kbrct';

const SCHEMA = 'schema';
const SKOS = 'skos';
const KB_RESOURCE = 'kbr';

function parseDataTridodpad(dataset, resultFileName) {
    const converted = JSON.parse(dataset);
    const features = converted['features'];
    const rdfBuilder = new RDFBuilder();

    rdfBuilder.addPrefixes([
        {prefix: 'rdf', iri: 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'},
        {prefix: 'rdfs', iri: 'http://www.w3.org/2000/01/rdf-schema#'},
        {prefix: KB_RESOURCE_TRIDODPAD, iri: 'http://www.kyrylo.bulat.com/resource/tridodpad/'},
        {prefix: KB_LOOKUPS_TRASH_TYPE_NAME, iri: "http://www.kyrylo.bulat.com/ciselniky/trashtypename"},
        {prefix: KB_RESOURCE_TRASH_TYPE_NAME, iri: "http://www.kyrylo.bulat.com/resource/trashtypename/"},
        {prefix: KB_LOOKUPS_CONTAINER_TYPE, iri: "http://www.kyrylo.bulat.com/resource/containertype"},
        {prefix: KB_RESOURCE_CONTAINER_TYPE, iri: "http://www.kyrylo.bulat.com/resource/containertype/"},
        {prefix: KB_RESOURCE, iri: "http://www.kyrylo.bulat.com/resource/"},
        {prefix: SCHEMA, iri: "http://schema.org/"},
        {prefix: SKOS, iri: "http://www.w3.org/2004/02/skos/core#"}
    ]);

    let trashTypeNameIndex = 0;
    const trashTypeNameIndexMap = {};

    let containerTypeIndex = 0;
    const containerTypeIndexMap = {};

    rdfBuilder.addLookup(KB_LOOKUPS_TRASH_TYPE_NAME, 'Cislenik typu odpadovych kosu');
    rdfBuilder.addLookup(KB_LOOKUPS_CONTAINER_TYPE, 'Cislenik typu kontejneru');

    features.forEach((feature, index) => {
        const properties = feature['properties'];
        const geometry = feature['geometry'];
        const longitude = geometry['coordinates'][0];
        const latitude = geometry['coordinates'][1];

        const trashTypeName = properties['TRASHTYPENAME'];
        if (trashTypeNameIndexMap[trashTypeName] === undefined) {
            trashTypeNameIndexMap[trashTypeName] = trashTypeNameIndex++;
        }
        const trashTypeNameSubject = `${KB_RESOURCE_TRASH_TYPE_NAME}:${trashTypeNameIndexMap[trashTypeName]}`;
        rdfBuilder.addConceptToLookup(KB_LOOKUPS_TRASH_TYPE_NAME, trashTypeNameSubject, trashTypeName, trashTypeName);

        const containerType = properties['CONTAINERTYPE'];
        if (containerTypeIndexMap[containerType] === undefined) {
            containerTypeIndexMap[containerType] = containerTypeIndex++;
        }
        const containerTypeSubject = `${KB_RESOURCE_CONTAINER_TYPE}:${containerTypeIndexMap[containerType]}`;
        rdfBuilder.addConceptToLookup(KB_LOOKUPS_CONTAINER_TYPE, containerTypeSubject, containerType, containerType);

        const subject = `${KB_RESOURCE_TRIDODPAD}:${index}`;
        rdfBuilder.addStatement(subject, {
            'a': "schema:Place",
            'kbr:objectid': properties['OBJECTID'],
            'kbr:stationid': properties['STATIONID'],
            'kbr:trashtypename': trashTypeNameSubject,
            'kbr:celaningfrequencycode': properties['CLEANINGFREQUENCYCODE'],
            'kbr:containertype': containerTypeSubject,
            'schema:geo': {
                'a': 'schema:GeoCoordinates',
                'schema:latitude': latitude,
                'schema:longitude': longitude
            }
        });
    });
    fs.writeFileSync(resultFileName, rdfBuilder.build());
}



const datasetTridodpad = fs.readFileSync('./dataset_tridodpad.json', 'utf8');
parseDataTridodpad(datasetTridodpad, 'result_tridodpad.ttl');
