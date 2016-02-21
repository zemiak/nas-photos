/* global navigationDocument, resourceLoaderLocal, Template */

var Presenter = {
    width: 293,
    height: 161,

    makeDocument: function(resource) {
        if (!Presenter.parser) {
            Presenter.parser = new DOMParser();
        }
        var doc = Presenter.parser.parseFromString(resource, "application/xml");
        return doc;
    },

    modalDialogPresenter: function(xml) {
        navigationDocument.presentModal(xml);
    },

    pushDocument: function(xml) {
        navigationDocument.pushDocument(xml);
    },

    replaceDocument: function(xml, doc) {
        navigationDocument.replaceDocument(xml, doc);
    },

    load: function(event) {
        var ele = event.target;

        var action = ele.getAttribute("action");
        if (action) {
            eval(action);
        }
    },

    getDocumentFromTemplate: function(template) {
        var template = resourceLoaderLocal.loadBundleResource("js/view/" + template + ".xml.js");
        eval(template);
        var res = Template.call(this);
        var doc = Presenter.makeDocument(res);
        doc.addEventListener("select", Presenter.load.bind(Presenter));

        return doc;
    },

    navigate: function(template) {
        var doc = Presenter.getDocumentFromTemplate(template);
        Presenter.pushDocument(doc);
    },

    navigateReplace: function(template) {
        var doc = Presenter.getDocumentFromTemplate(template);
        Presenter.replaceDocument(doc, Presenter.getCurrentDocument());
    },

    getDocumentName: function() {
        var ele = Presenter.getDocumentDataElement();
        return ele.getAttribute("data-template");
    },

    getDocumentDataElement: function() {
        var document = Presenter.getCurrentDocument();
        return document.getElementById("template-data");
    },

    getCurrentDocument: function() {
        return navigationDocument.documents[navigationDocument.documents.length - 1];
    }
};
