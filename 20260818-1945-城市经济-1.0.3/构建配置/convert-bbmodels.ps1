param(
    [Parameter(Mandatory = $true)]
    [string]$ProjectRoot
)

$ErrorActionPreference = 'Stop'

function Convert-BbModel {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Source,
        [Parameter(Mandatory = $true)]
        [string]$TargetName,
        [double]$YScale = 1.0
    )

    $model = Get-Content -Raw -LiteralPath $Source | ConvertFrom-Json
    $uvScaleX = 16.0 / [double]$model.resolution.width
    $uvScaleY = 16.0 / [double]$model.resolution.height
    $elements = [System.Collections.Generic.List[object]]::new()

    foreach ($sourceElement in $model.elements) {
        $element = [ordered]@{
            from = @(
                [double]$sourceElement.from[0]
                ([double]$sourceElement.from[1]) * $YScale
                [double]$sourceElement.from[2]
            )
            to = @(
                [double]$sourceElement.to[0]
                ([double]$sourceElement.to[1]) * $YScale
                [double]$sourceElement.to[2]
            )
        }

        if ($null -ne $sourceElement.rotation -and $null -ne $sourceElement.rotation.axis) {
            $element['rotation'] = [ordered]@{
                origin = @(
                    [double]$sourceElement.origin[0]
                    ([double]$sourceElement.origin[1]) * $YScale
                    [double]$sourceElement.origin[2]
                )
                axis = [string]$sourceElement.rotation.axis
                angle = [double]$sourceElement.rotation.angle
                rescale = [bool]$sourceElement.rescale
            }
        }

        $faces = [ordered]@{}
        foreach ($faceName in @('north', 'east', 'south', 'west', 'up', 'down')) {
            $sourceFace = $sourceElement.faces.$faceName
            if ($null -eq $sourceFace -or $null -eq $sourceFace.texture) {
                continue
            }

            $u0 = ([double]$sourceFace.uv[0]) * $uvScaleX
            $v0 = ([double]$sourceFace.uv[1]) * $uvScaleY
            $u1 = ([double]$sourceFace.uv[2]) * $uvScaleX
            $v1 = ([double]$sourceFace.uv[3]) * $uvScaleY
            $face = [ordered]@{
                uv = @($u0, $v0, $u1, $v1)
                texture = '#base'
            }
            if ($null -ne $sourceFace.rotation) {
                $face['rotation'] = [int]$sourceFace.rotation
            }
            $faces[$faceName] = $face
        }
        $element['faces'] = $faces
        $elements.Add($element)
    }

    $textureSource = [string]$model.textures[0].source
    $base64 = $textureSource.Substring($textureSource.IndexOf(',') + 1)
    $texturePath = Join-Path $ProjectRoot "src/main/resources/assets/cityec/textures/block/$TargetName.png"
    [IO.File]::WriteAllBytes($texturePath, [Convert]::FromBase64String($base64))

    $minecraftModel = [ordered]@{
        credit = 'Converted from supplied Blockbench model'
        ambientocclusion = $true
        textures = [ordered]@{
            base = "cityec:block/$TargetName"
            particle = "cityec:block/$TargetName"
        }
        elements = $elements
    }
    $modelPath = Join-Path $ProjectRoot "src/main/resources/assets/cityec/models/block/$TargetName.json"
    $minecraftModel | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $modelPath -Encoding utf8
}

$downloads = 'C:\Users\Administrator\Downloads'
Convert-BbModel -Source (Join-Path $downloads 'auto_teller_machine_off.bbmodel') -TargetName 'auto_teller_machine_bb_off'
Convert-BbModel -Source (Join-Path $downloads 'auto_teller_machine_on.bbmodel') -TargetName 'auto_teller_machine_bb_on'
Convert-BbModel -Source (Join-Path $downloads 'cash_register_off.bbmodel') -TargetName 'cash_register_bb_off' -YScale 3.0
Convert-BbModel -Source (Join-Path $downloads 'cash_register_on.bbmodel') -TargetName 'cash_register_bb_on' -YScale 3.0
