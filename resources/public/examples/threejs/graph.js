export const graph =
{
    "entities": {
        "tick": {
            "id": "tick",
            "meta": {
                "ui": {
                    "x": 204,
                    "y": 226
                }
            }
        },
        "color": {
            "id": "color",
            "value": "0xff0000",
            "meta": {
                "ui": {
                    "y": -547,
                    "x": -537
                }
            }
        },
        "rot-speed-x": {
            "id": "rot-speed-x",
            "value": 0.01,
            "meta": {
                "ui": {
                    "x": -146,
                    "y": 312
                }
            }
        },
        "camera-position": {
            "id": "camera-position",
            "value": [
                0,
                0,
                700
            ],
            "meta": {
                "ui": {
                    "y": -549,
                    "x": -92
                }
            }
        },
        "geometry": {
            "id": "geometry",
            "meta": {
                "ui": {
                    "y": -246,
                    "x": -357
                }
            }
        },
        "renderer": {
            "id": "renderer",
            "meta": {
                "ui": {
                    "y": -321,
                    "x": 410
                }
            }
        },
        "size": {
            "id": "size",
            "value": {
                "width": 800,
                "height": 500
            },
            "meta": {
                "ui": {
                    "y": -690,
                    "x": 88
                }
            }
        },
        "box-dimensions": {
            "id": "box-dimensions",
            "value": [
                200,
                200,
                200
            ],
            "meta": {
                "ui": {
                    "x": -358,
                    "y": -549
                }
            }
        },
        "rot-speed-y": {
            "id": "rot-speed-y",
            "value": 0.02,
            "meta": {
                "ui": {
                    "x": -290,
                    "y": 316
                }
            }
        },
        "mesh": {
            "id": "mesh",
            "meta": {
                "ui": {
                    "y": 3,
                    "x": -152
                }
            }
        },
        "material": {
            "id": "material",
            "meta": {
                "ui": {
                    "y": -249,
                    "x": -536
                }
            }
        },
        "camera": {
            "id": "camera",
            "meta": {
                "ui": {
                    "y": -360,
                    "x": 93
                }
            }
        },
        "scene": {
            "id": "scene",
            "meta": {
                "ui": {
                    "y": -119,
                    "x": 96
                }
            }
        }
    },
    "processes": {
        "animation-frame": {
            "id": "animation-frame",
            "ports": {},
            "code": "function(ports, send) {\n\tvar runing = true\n\t\n\tfunction tick() {\n\t\tsend()\n\t\tif (runing) {\n\t\t\trequestAnimationFrame(tick)\n\t\t}\n\t}\n\t\n\ttick()\n\t\n\treturn function() {\n\t\truning = false\n\t}\n}",
            "autostart": true,
            "async": true,
            "meta": {
                "ui": {
                    "x": 208,
                    "y": 344
                }
            }
        },
        "update-mesh": {
            "id": "update-mesh",
            "ports": {
                "mesh": "accumulator",
                "material": "hot",
                "geometry": "hot"
            },
            "code": "function(ports) {\n\tvar mesh = ports.mesh,\n\t\t\tmat = ports.material\n\t\t\t\n\tmesh.material = mat\n\tmesh.geometry = ports.geometry\n\treturn mesh\n}",
            "meta": {
                "ui": {
                    "y": 1,
                    "x": -276
                }
            }
        },
        "create-material": {
            "id": "create-material",
            "ports": {
                "color": "hot"
            },
            "code": "function(ports) {\n\treturn new this.three.MeshBasicMaterial({\n\t\tcolor: parseInt(ports.color),\n\t\twireframe: true\n\t})\n}",
            "meta": {
                "ui": {
                    "y": -362,
                    "x": -535
                }
            }
        },
        "create-scene": {
            "id": "create-scene",
            "ports": {
                "mesh": "hot"
            },
            "code": "function(ports) {\n\tvar scene = new this.three.Scene()\n\tscene.add(ports.mesh)\n\treturn scene\n}",
            "meta": {
                "ui": {
                    "y": -2,
                    "x": 97
                }
            }
        },
        "rotate-mesh": {
            "id": "rotate-mesh",
            "ports": {
                "rot_x": "cold",
                "rot_y": "cold",
                "tick": "hot",
                "mesh": "cold"
            },
            "code": "function(ports) {\n\tports.mesh.rotation.x += ports.rot_x\n\tports.mesh.rotation.y += ports.rot_y\n\treturn ports.mesh\n}",
            "meta": {
                "ui": {
                    "x": -149,
                    "y": 124
                }
            }
        },
        "create-renderer": {
            "id": "create-renderer",
            "ports": {},
            "code": "function(ports) {\n\tvar renderer = new this.three.WebGLRenderer()\n\t\n\tdocument.body.appendChild(renderer.domElement)\n\t\n\treturn renderer\n}",
            "autostart": true,
            "meta": {
                "ui": {
                    "y": -437,
                    "x": 408
                }
            }
        },
        "render": {
            "id": "render",
            "ports": {
                "scene": "cold",
                "renderer": "cold",
                "camera": "cold",
                "tick": "hot"
            },
            "code": "function(ports) {\n\tports.renderer.render(ports.scene, ports.camera)\n}",
            "meta": {
                "ui": {
                    "y": -119,
                    "x": 413
                }
            }
        },
        "create-mesh": {
            "id": "create-mesh",
            "ports": {},
            "code": "function(ports) {\n\treturn new this.three.Mesh()\n}",
            "autostart": true,
            "meta": {
                "ui": {
                    "y": -108,
                    "x": -154
                }
            }
        },
        "create-geometry": {
            "id": "create-geometry",
            "ports": {
                "dimensions": "hot"
            },
            "code": "function(ports) {\n\tvar size = ports.dimensions;\n\treturn new this.three.BoxGeometry(size[0], size[1], size[2])\n}",
            "meta": {
                "ui": {
                    "y": -362,
                    "x": -357
                }
            }
        },
        "update-size": {
            "id": "update-size",
            "ports": {
                "renderer": "hot",
                "size": "hot"
            },
            "code": "function(ports) {\n\tports.renderer.setSize(ports.size.width, ports.size.height)\n}",
            "meta": {
                "ui": {
                    "y": -497,
                    "x": 253
                }
            }
        },
        "create-camera": {
            "id": "create-camera",
            "ports": {
                "size": "hot"
            },
            "code": "function(ports) {\n\treturn new this.three.PerspectiveCamera(\n\t\t75, ports.size.width / ports.size.height, 1, 2000\n\t)\n}",
            "meta": {
                "ui": {
                    "y": -479,
                    "x": 91
                }
            }
        },
        "update-camera": {
            "id": "update-camera",
            "ports": {
                "camera": "accumulator",
                "position": "hot"
            },
            "code": "function(ports) {\n\tvar cam = ports.camera,\n\t\t\tpos = ports.position\n\t\n\tcam.position.x = pos[0]\n\tcam.position.y = pos[1]\n\tcam.position.z = pos[2]\n\t\n\treturn cam\n}",
            "meta": {
                "ui": {
                    "y": -363,
                    "x": -37
                }
            }
        }
    },
    "arcs": {
        "size->create-camera::size": {
            "id": "size->create-camera::size",
            "entity": "size",
            "process": "create-camera",
            "port": "size",
            "meta": {}
        },
        "rot-speed-y->rotate-mesh::rot_y": {
            "id": "rot-speed-y->rotate-mesh::rot_y",
            "entity": "rot-speed-y",
            "process": "rotate-mesh",
            "port": "rot_y",
            "meta": {}
        },
        "create-material->material": {
            "id": "create-material->material",
            "entity": "material",
            "process": "create-material",
            "meta": {}
        },
        "scene->render::scene": {
            "id": "scene->render::scene",
            "entity": "scene",
            "process": "render",
            "port": "scene",
            "meta": {}
        },
        "update-mesh->mesh": {
            "id": "update-mesh->mesh",
            "entity": "mesh",
            "process": "update-mesh",
            "meta": {}
        },
        "create-mesh->mesh": {
            "id": "create-mesh->mesh",
            "entity": "mesh",
            "process": "create-mesh",
            "meta": {}
        },
        "create-scene->scene": {
            "id": "create-scene->scene",
            "entity": "scene",
            "process": "create-scene",
            "meta": {}
        },
        "tick->rotate-mesh::tick": {
            "id": "tick->rotate-mesh::tick",
            "entity": "tick",
            "process": "rotate-mesh",
            "port": "tick",
            "meta": {}
        },
        "animation-frame->tick": {
            "id": "animation-frame->tick",
            "entity": "tick",
            "process": "animation-frame",
            "meta": {}
        },
        "rot-speed-x->rotate-mesh::rot_x": {
            "id": "rot-speed-x->rotate-mesh::rot_x",
            "entity": "rot-speed-x",
            "process": "rotate-mesh",
            "port": "rot_x",
            "meta": {}
        },
        "geometry->update-mesh::geometry": {
            "id": "geometry->update-mesh::geometry",
            "entity": "geometry",
            "process": "update-mesh",
            "port": "geometry",
            "meta": {}
        },
        "update-camera->camera": {
            "id": "update-camera->camera",
            "entity": "camera",
            "process": "update-camera",
            "meta": {}
        },
        "create-renderer->renderer": {
            "id": "create-renderer->renderer",
            "entity": "renderer",
            "process": "create-renderer",
            "meta": {}
        },
        "tick->render::tick": {
            "id": "tick->render::tick",
            "entity": "tick",
            "process": "render",
            "port": "tick",
            "meta": {}
        },
        "create-camera->camera": {
            "id": "create-camera->camera",
            "entity": "camera",
            "process": "create-camera",
            "meta": {}
        },
        "mesh->create-scene::mesh": {
            "id": "mesh->create-scene::mesh",
            "entity": "mesh",
            "process": "create-scene",
            "port": "mesh",
            "meta": {}
        },
        "renderer->render::renderer": {
            "id": "renderer->render::renderer",
            "entity": "renderer",
            "process": "render",
            "port": "renderer",
            "meta": {}
        },
        "camera-position->update-camera::position": {
            "id": "camera-position->update-camera::position",
            "entity": "camera-position",
            "process": "update-camera",
            "port": "position",
            "meta": {}
        },
        "size->update-size::size": {
            "id": "size->update-size::size",
            "entity": "size",
            "process": "update-size",
            "port": "size",
            "meta": {}
        },
        "material->update-mesh::material": {
            "id": "material->update-mesh::material",
            "entity": "material",
            "process": "update-mesh",
            "port": "material",
            "meta": {}
        },
        "color->create-material::color": {
            "id": "color->create-material::color",
            "entity": "color",
            "process": "create-material",
            "port": "color",
            "meta": {}
        },
        "camera->render::camera": {
            "id": "camera->render::camera",
            "entity": "camera",
            "process": "render",
            "port": "camera",
            "meta": {}
        },
        "mesh->rotate-mesh::mesh": {
            "id": "mesh->rotate-mesh::mesh",
            "entity": "mesh",
            "process": "rotate-mesh",
            "port": "mesh",
            "meta": {}
        },
        "box-dimensions->create-geometry::dimensions": {
            "id": "box-dimensions->create-geometry::dimensions",
            "entity": "box-dimensions",
            "process": "create-geometry",
            "port": "dimensions",
            "meta": {}
        },
        "renderer->update-size::renderer": {
            "id": "renderer->update-size::renderer",
            "entity": "renderer",
            "process": "update-size",
            "port": "renderer",
            "meta": {}
        },
        "create-geometry->geometry": {
            "id": "create-geometry->geometry",
            "entity": "geometry",
            "process": "create-geometry",
            "meta": {}
        }
    },
    "meta": {
        "ui": {
            "layout": []
        }
    }
}
