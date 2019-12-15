const fs = require('fs');

const RDFBuilder = require('./RDFBuilder');
const KB_RESOURCE_PARKOMATY = 'kbrp';
const KB_RESOURCE = 'kbr';
const KB_RESOURCE_ORGANIZACE = 'kbo';
const KB_LOOKUPS_MESTSKA_CAST = 'kblmc';
const KB_RESOURCE_MESTSKA_CAST = 'kbrmc';
const SCHEMA = 'schema';
const SKOS = 'skos';

function parseDataSetParkomaty(dataset, resultFileName) {
    const converted = JSON.parse(dataset);

    const features = converted['features'];
    const rdfBuilder = new RDFBuilder();

    rdfBuilder.addPrefixes([
        {prefix: 'rdf', iri: 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'},
        {prefix: 'rdfs', iri: 'http://www.w3.org/2000/01/rdf-schema#'},
        {prefix: KB_RESOURCE_PARKOMATY, iri: 'http://www.kyrylo.bulat.com/resource/parkomaty/'},
        {prefix: KB_RESOURCE, iri: "http://www.kyrylo.bulat.com/resource/"},
        {prefix: KB_RESOURCE_ORGANIZACE, iri: "http://www.kyrylo.bulat.com/resource/organizace/"},
        {prefix: KB_LOOKUPS_MESTSKA_CAST, iri: "http://www.kyrylo.bulat.com/ciselniky/mestskacast"},
        {prefix: KB_RESOURCE_MESTSKA_CAST, iri: "http://www.kyrylo.bulat.com/resource/mestskacasti/"},
        {prefix: SCHEMA, iri: "http://schema.org/"},
        {prefix: SKOS, iri: "http://www.w3.org/2004/02/skos/core#"}
    ]);

    let organizationIndex = 0;
    const organizationIndexMap = {};

    let mestskeCastiIndex = 0;
    const mestskeCastiIndexMap = {};

    rdfBuilder.addLookup(KB_LOOKUPS_MESTSKA_CAST, 'Cislenik Mestkych casti Prahy');

    features.forEach((feature, index) => {
        const properties = feature['properties'];
        const geometry = feature['geometry'];
        const longitude = geometry['coordinates'][0];
        const latitude = geometry['coordinates'][1];

        const poskyt = properties['POSKYT'];
        if (organizationIndexMap[poskyt] === undefined) {
            organizationIndexMap[poskyt] = organizationIndex++;
        }

        const poskytSubject = `${KB_RESOURCE_ORGANIZACE}:${organizationIndexMap[poskyt]}`;
        rdfBuilder.addEntity(poskytSubject, {
            'a': "schema:Organization",
            'schema:identifier': `"${poskyt}"@cs`
        }, false);

        const px = properties['PX'];
        if (mestskeCastiIndexMap[px] === undefined) {
            mestskeCastiIndexMap[px] = mestskeCastiIndex++;
        }
        const pxSubject = `${KB_RESOURCE_MESTSKA_CAST}:${mestskeCastiIndexMap[px]}`;
        rdfBuilder.addConceptToLookup(KB_LOOKUPS_MESTSKA_CAST, pxSubject, px, px);

        const subject = `${KB_RESOURCE_PARKOMATY}:${index}`;
        rdfBuilder.addStatement(subject, {
            'a': "schema:ParkingFacility",
            'kbr:objectid': properties['OBJECTID'],
            'kbr:poskyt': poskytSubject,
            // PA is the same as CODE, but like a String - I don't need this in my structure
            // 'kb:pa': `"${properties['PA']}"`,
            'schema:containedInPlace': pxSubject,
            'schema:address': `"${properties['STREET']}"@cs`,
            'schema:branchCode': properties['CODE'],
            'schema:geo': {
                'a': 'schema:GeoCoordinates',
                'schema:latitude': latitude,
                'schema:longitude': longitude
            }
        });
    });
    fs.writeFileSync(resultFileName, rdfBuilder.build());
}

const datasetParkomaty = fs.readFileSync('./dataset_parkomaty.json', 'utf8');
parseDataSetParkomaty(datasetParkomaty, 'result_parkomaty.ttl');
