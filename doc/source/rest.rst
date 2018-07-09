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

.. http:post:: /annotations

    Train a dataset with a given algorithm id. The training process can be
    quite large, so this REST method uses a asynchronous model to perform
    each request.

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

    The response of this method will only be a ``202 ACCEPTED`` status code, with
    the ``Location:`` header filled with the task path element. See ``/tasks``
    collection to get more information about how tasks are managed on the
    service.

    The dataset must be in a 'untrained' (0) state to get this operation done.
    Also, no operation such as ``add_triples`` must be being processed.
    Otherwise, a 409 CONFLICT status code will be obtained.

    :param int dataset_id: Unique *dataset_id*
    :query int id_algorithm: The wanted algorithm to train the dataset
    :statuscode 202: The requests has been accepted to the system and a task has
                     been created. See Location header to get more information.
    :statuscode 404: The dataset or the algorithm can't be found.
    :statuscode 409: The dataset cannot be trained due to its status.
