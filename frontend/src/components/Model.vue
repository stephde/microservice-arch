<template>
    
</template>

<script>
// inspired by https://github.com/NorthwoodsSoftware/GoJS/blob/master/samples/vue.html
    import * as go from 'gojs'
    const make = go.GraphObject.make

    export default {
        name: "Model",
        props: ["modelData"],  // accept model data as a parameter
        mounted: function() {
            const self = this;
            const myDiagram =
                make(go.Diagram, this.$el,
                    {
                        layout: make(go.TreeLayout, { angle: 90, arrangement: go.TreeLayout.ArrangementHorizontal }),
                        "undoManager.isEnabled": true,
                        // Model ChangedEvents get passed up to component users
                        "ModelChanged": function(e) { self.$emit("model-changed", e); },
                        "ChangedSelection": function(e) { self.$emit("changed-selection", e); }
                    });
            myDiagram.nodeTemplate =
                make(go.Node, "Auto",
                    make(go.Shape,
                        {
                            fill: "white", strokeWidth: 0,
                            portId: "", fromLinkable: true, toLinkable: true, cursor: "pointer"
                        },
                        new go.Binding("fill", "color")),
                    make(go.TextBlock,
                        { margin: 8, editable: true },
                        new go.Binding("text").makeTwoWay())
                );
            myDiagram.linkTemplate =
                make(go.Link,
                    { relinkableFrom: true, relinkableTo: true },
                    make(go.Shape),
                    make(go.Shape, { toArrow: "OpenTriangle" })
                );
            this.diagram = myDiagram;
            this.updateModel(this.modelData);
        },
        watch: {
            modelData: function(val) { this.updateModel(val); }
        },
        methods: {
            model: function() { return this.diagram.model; },
            updateModel: function(val) {
                // No GoJS transaction permitted when replacing Diagram.model.
                if (val instanceof go.Model) {
                    this.diagram.model = val;
                } else {
                    const m = new go.GraphLinksModel()
                    if (val) {
                        for (var p in val) {
                            m[p] = val[p];
                        }
                    }
                    this.diagram.model = m;
                }
            },
            updateDiagramFromData: function() {
                this.diagram.startTransaction();
                // This is very general but very inefficient.
                // It would be better to modify the diagramData data by calling
                // Model.setDataProperty or Model.addNodeData, et al.
                this.diagram.updateAllRelationshipsFromData();
                this.diagram.updateAllTargetBindings();
                this.diagram.commitTransaction("updated");
            }
        }
    }
</script>

<style scoped>

</style>