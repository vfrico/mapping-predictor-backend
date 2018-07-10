.. _restservice:


REST Service
============


Annotations
-----------

.. http:get:: /annotations/(int:annotation_id)/

    Get a full annotation

    **Sample request and response**

    :http:get:`/annotations/1/`

    .. sourcecode:: json

        {
            "attributeA": "nationality",
            "attributeB": "lugar_de_fallecimiento",
            "c1": 1.0,
            "c2": 1.0,
            "c3a": 1.0,
            "c3b": 1.0,
            "classA": "http://dbpedia.org/ontology/Scientist",
            "classB": "http://dbpedia.org/ontology/Scientist",
            "domainPropA": "dbo:Person",
            "domainPropB": "dbo:Person",
            "langA": "en",
            "langB": "es",
            "m1": 621,
            "m2": 709,
            "m3": 719,
            "m4a": 643,
            "m4b": 493,
            "m5a": 719,
            "m5b": 719,
            "propA": "dbo:nationality",
            "propB": "dbo:deathPlace",
            "rangePropA": "dbo:Country",
            "rangePropB": "dbo:Place",
            "tb1": 0,
            "tb10": 1,
            "tb11": 0,
            "tb2": 0,
            "tb3": 1,
            "tb4": 1,
            "tb5": 1,
            "tb6": 1,
            "tb7": 1,
            "tb8": 1,
            "tb9": 0,
            "templateA": "Infobox_scientist",
            "templateB": "Ficha_de_científico",
            "id": 15098
        }

    :param int annotation_id: Unique *annotation_id*
    :statuscode 200: Returns all information about an annotation.
    :statuscode 404: The annotation can't be found.

.. http:get:: /annotations

    Download all the annotations as a JSON or CSV.

    On the headers of the HTTP request, it is possible to specify the format with:

    .. sourcecode::
        Accept: text/plain
        Accept: application/json

    :param str langa: Language A (Usually 'en')
    :param str langb: Language B (Use the shortest ISO code available)
    :statuscode 400: Parameters has not been correctly established


.. http:post:: /annotations

    Add an annotation to the dataset. This endpoint should be used only by the service that generates annotations once
    a dbpedia release has been published.

    **Sample request and response**

    :http:post:`/annotations`

    .. sourcecode:: json

        {
            "attributeA": "nationality",
            "attributeB": "lugar_de_fallecimiento",
            "c1": 1.0,
            "c2": 1.0,
            "c3a": 1.0,
            "c3b": 1.0,
            "classA": "http://dbpedia.org/ontology/Scientist",
            "classB": "http://dbpedia.org/ontology/Scientist",
            "domainPropA": "dbo:Person",
            "domainPropB": "dbo:Person",
            "langA": "en",
            "langB": "es",
            "m1": 621,
            "m2": 709,
            "m3": 719,
            "m4a": 643,
            "m4b": 493,
            "m5a": 719,
            "m5b": 719,
            "propA": "dbo:nationality",
            "propB": "dbo:deathPlace",
            "rangePropA": "dbo:Country",
            "rangePropB": "dbo:Place",
            "tb1": 0,
            "tb10": 1,
            "tb11": 0,
            "tb2": 0,
            "tb3": 1,
            "tb4": 1,
            "tb5": 1,
            "tb6": 1,
            "tb7": 1,
            "tb8": 1,
            "tb9": 0,
            "templateA": "Infobox_scientist",
            "templateB": "Ficha_de_científico",
        }


    :statuscode 201: The request has been accepted by the system and a new annotation is stored on DB.
