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
        [double]$YScale = 1.0,
        [switch]$OpaqueBottom,
        [switch]$FullBottom,
        [double]$BottomFromX = 0.0,
        [double]$BottomToX = 16.0,
        [double]$BottomFromZ = 0.0,
        [double]$BottomToZ = 16.0
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

        $rotationAxis = $null
        $rotationAngle = 0.0
        $rotationValues = @($sourceElement.rotation)
        if ($rotationValues.Count -ge 3) {
            if ([Math]::Abs([double]$rotationValues[0]) -gt 0.0001) {
                $rotationAxis = 'x'
                $rotationAngle = [double]$rotationValues[0]
            } elseif ([Math]::Abs([double]$rotationValues[1]) -gt 0.0001) {
                $rotationAxis = 'y'
                $rotationAngle = [double]$rotationValues[1]
            } elseif ([Math]::Abs([double]$rotationValues[2]) -gt 0.0001) {
                $rotationAxis = 'z'
                $rotationAngle = [double]$rotationValues[2]
            }
        } elseif ($null -ne $sourceElement.rotation -and $null -ne $sourceElement.rotation.axis) {
            $rotationAxis = [string]$sourceElement.rotation.axis
            $rotationAngle = [double]$sourceElement.rotation.angle
        }

        if ($null -ne $rotationAxis) {
            $element['rotation'] = [ordered]@{
                origin = @(
                    [double]$sourceElement.origin[0]
                    ([double]$sourceElement.origin[1]) * $YScale
                    [double]$sourceElement.origin[2]
                )
                axis = $rotationAxis
                angle = $rotationAngle
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
            $faceTexture = if ($OpaqueBottom -and $faceName -eq 'down') { '#bottom' } else { '#base' }
            $face = [ordered]@{
                uv = @($u0, $v0, $u1, $v1)
                texture = $faceTexture
            }
            if ($null -ne $sourceFace.rotation) {
                $face['rotation'] = [int]$sourceFace.rotation
            }
            $faces[$faceName] = $face
        }
        $element['faces'] = $faces
        $elements.Add($element)
    }

    if ($FullBottom) {
        $bottomFaces = [ordered]@{}
        foreach ($faceName in @('north', 'east', 'south', 'west', 'up', 'down')) {
            $bottomFaces[$faceName] = [ordered]@{
                uv = @(0, 0, 16, 16)
                texture = '#bottom'
            }
        }
        $elements.Add([ordered]@{
            from = @($BottomFromX, -0.05, $BottomFromZ)
            to = @($BottomToX, 0.05, $BottomToZ)
            faces = $bottomFaces
        })
    }

    $textureSource = [string]$model.textures[0].source
    $base64 = $textureSource.Substring($textureSource.IndexOf(',') + 1)
    $texturePath = Join-Path $ProjectRoot "src/main/resources/assets/cityec/textures/block/$TargetName.png"
    [IO.File]::WriteAllBytes($texturePath, [Convert]::FromBase64String($base64))

    $textures = [ordered]@{
        base = "cityec:block/$TargetName"
        particle = "cityec:block/$TargetName"
    }
    if ($OpaqueBottom -or $FullBottom) {
        $textures['bottom'] = "cityec:block/$TargetName`_bottom"
    }
    $minecraftModel = [ordered]@{
        credit = 'Converted from supplied Blockbench model'
        ambientocclusion = $true
        textures = $textures
        elements = $elements
    }
    $modelPath = Join-Path $ProjectRoot "src/main/resources/assets/cityec/models/block/$TargetName.json"
    $minecraftModel | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $modelPath -Encoding utf8
}

$downloads = 'C:\Users\Administrator\Downloads'
Convert-BbModel -Source (Join-Path $downloads 'auto_teller_machine_off.bbmodel') -TargetName 'auto_teller_machine_bb_off' -OpaqueBottom
Convert-BbModel -Source (Join-Path $downloads 'auto_teller_machine_on.bbmodel') -TargetName 'auto_teller_machine_bb_on' -OpaqueBottom
Convert-BbModel -Source (Join-Path $downloads 'cash_register_off.bbmodel') -TargetName 'cash_register_bb_off' -YScale 1.0 -OpaqueBottom
Convert-BbModel -Source (Join-Path $downloads 'cash_register_on.bbmodel') -TargetName 'cash_register_bb_on' -YScale 1.0 -OpaqueBottom

Add-Type -AssemblyName System.Drawing
function New-OpaqueBottomTexture {
    param(
        [string]$Path,
        [System.Drawing.Color]$Color
    )
    $texture = [System.Drawing.Bitmap]::new(16, 16)
    for ($x = 0; $x -lt 16; $x++) {
        for ($y = 0; $y -lt 16; $y++) { $texture.SetPixel($x, $y, $Color) }
    }
    $texture.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $texture.Dispose()
}

$textureDirectory = Join-Path $ProjectRoot 'src/main/resources/assets/cityec/textures/block'
New-OpaqueBottomTexture -Path (Join-Path $textureDirectory 'auto_teller_machine_bb_off_bottom.png') -Color ([System.Drawing.Color]::FromArgb(255, 96, 96, 96))
New-OpaqueBottomTexture -Path (Join-Path $textureDirectory 'auto_teller_machine_bb_on_bottom.png') -Color ([System.Drawing.Color]::FromArgb(255, 96, 96, 96))
New-OpaqueBottomTexture -Path (Join-Path $textureDirectory 'cash_register_bb_off_bottom.png') -Color ([System.Drawing.Color]::FromArgb(255, 96, 96, 96))
New-OpaqueBottomTexture -Path (Join-Path $textureDirectory 'cash_register_bb_on_bottom.png') -Color ([System.Drawing.Color]::FromArgb(255, 96, 96, 96))
