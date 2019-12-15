const TAB = '   ';
const SPACE = ' ';
const NEW_LINE = '\n';
const SEMICOLON = ';';
const PREFIX = '@prefix';
const COLON = ':';
const LESS_THAN = '<';
const GREATER_THAN = '>';
const DOT = '.';

class RDFBuilder {


    constructor() {
        this.prefixes = [];
        this.entities = [];
        this.lookups = [];
        this.statements = [];
    }

    addPrefix(prefix) {
        this.prefixes.push(prefix);
    }

    addPrefixes(prefixes) {
        prefixes.forEach(prefix => {
            this.addPrefix(prefix);
        })
    }

    /**
     *
     * @param subject
     * @param properties
     * @param duplicate defines whether we want to create duplicate statement about this entity
     */
    addEntity(subject, properties, duplicate) {
        // if entity is not yet defined or we want to duplicate statement about entity - then create a statement otherwise ignore adding a new entity
        if (!this.checkAlreadyDefined(this.entities, subject) || duplicate) {
            this.entities.push({
                subject: subject,
                properties: properties
            });
        }
    }

    checkAlreadyDefined(statements, subject) {
        for (let i = 0; i < statements.length; i++) {
            if (statements[i].subject === subject) {
                return true;
            }
        }
        return false;
    }

    addLookup(lookupSubject, preferredLabel) {
        this.addLookupInternal(`${lookupSubject}:`, {
            'a': 'skos:ConceptScheme',
            'skos:prefLabel': `"${preferredLabel}"@cs`
        }, false);

    }

    addLookupInternal(lookupSubject, properties, duplicate) {
        // if lookup is not yet defined or we want to duplicate statement about lookup - then create a statement otherwise ignore adding a new lookup
        if (!this.checkAlreadyDefined(this.lookups, lookupSubject) || duplicate) {
            this.lookups.push({
                subject: lookupSubject,
                properties: properties,
                concepts: []
            });
        }
    }

    addConceptToLookup(lookup, conceptSubject, notation, prefLabel) {
        this.addConceptToLookupInternal(`${lookup}:`, {
            subject: conceptSubject,
            properties: {
                'a': 'skos:Concept',
                'skos:inScheme': `${lookup}:`,
                'skos:notation': `"${notation}"@cs`,
                'skos:prefLabel': `"${prefLabel}"@cs`
            }
        });

    }

    addConceptToLookupInternal(lookupSubject, concept) {
        this.lookups.forEach(lookup => {
            if (lookup.subject === lookupSubject) {
                if (!this.checkAlreadyDefined(lookup.concepts, concept.subject)) {
                    lookup.concepts.push(concept);
                }
            }
        })
    }


    addStatement(subject, properties) {
        this.statements.push({
            subject: subject,
            properties: properties
        });
    }

    build() {
        let returnResult = "";
        this.prefixes.forEach(prefix => {
            const result = [];
            result.push(PREFIX, prefix.prefix + COLON, LESS_THAN + prefix.iri + GREATER_THAN, DOT, NEW_LINE);
            returnResult += result.join(SPACE);
        });
        this.entities.forEach(entity => {
            returnResult += this.processStatement(entity);
        });
        this.lookups.forEach(lookup => {
            returnResult += this.processStatement(lookup);
            lookup.concepts.forEach(concept => {
                returnResult += this.processStatement(concept);
            })
        });
        this.statements.forEach(statement => {
            returnResult += this.processStatement(statement);
        });
        return returnResult;
    }

    processStatement(statement) {
        const result = [];
        result.push(statement.subject, NEW_LINE);
        const properties = statement.properties;
        for (const key in properties) {
            if (properties.hasOwnProperty(key)) {
                const value = properties[key];
                if (typeof value === 'object' && value !== null) {
                    // process blank node :D
                    result.push(TAB, key, SPACE, '[', SPACE, NEW_LINE);
                    for (const blankKey in value) {
                        if (value.hasOwnProperty(blankKey)) {
                            result.push(TAB, TAB, blankKey, SPACE, value[blankKey], SPACE, SEMICOLON, NEW_LINE);
                        }
                    }
                    result.pop();
                    result.pop();
                    result.push(']', SPACE, SEMICOLON, NEW_LINE);
                } else {
                    // property value ;
                    result.push(TAB, key, SPACE, value, SPACE, SEMICOLON, NEW_LINE);
                }
            }
        }
        // remove last new line and semicolon
        result.pop();
        result.pop();
        result.push(DOT, NEW_LINE);
        return result.join("");
    }
}

module.exports = RDFBuilder;
