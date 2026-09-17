$ErrorActionPreference = 'Stop'
$root = Join-Path $PSScriptRoot '../../app/src/main/res/drawable'
# Vector sources: solid silhouettes, inset light and consistent metal edging.
$icons = @{
 'ic_nav_shop_minimal' = @('M3,8L6,4H18L21,8V20H3Z','M3,8H21M9,4V8M15,4V8M9,12H15V16H9Z','#72E4FF')
 'ic_nav_quests_minimal' = @('M6,3H18L21,6V21H3V6Z','M8,3V6H16V3M7,11L9,13L12,10M14,11H17M7,17H17','#72E4FF')
 'ic_nav_hangar_minimal' = @('M2,21V9L12,2L22,9V21Z','M6,21V10H18V21M9,16L12,11L15,16L12,15ZM9,19H15','#72E4FF')
 'ic_nav_stats_minimal' = @('M3,3H18L21,6V21H3Z','M7,17V13M12,17V9M17,17V6M6,20H18','#72E4FF')
 'ic_nav_prestige_minimal' = @('M12,2L21,7V17L12,22L3,17V7Z','M12,5L17,12L12,19L7,12ZM4,7L7,8M17,16L20,17','#B49CFF')
 'ic_nav_settings_minimal' = @('M9,2H15L16,5L19,5L22,10L20,12L22,15L19,20L16,19L15,22H9L8,19L5,20L2,15L4,12L2,10L5,5H8Z','M16,12A4,4 0,1 1,8 12A4,4 0,1 1,16 12M12,10V14M10,12H14','#72E4FF')
 'ic_goal_route_minimal' = @('M3,3H18L21,6V21H3Z','M6,17L10,11L15,14L19,7M6,16V18M10,10V12M15,13V15M18,6H20V8','#72E4FF')
 'ic_achievement_medal' = @('M5,2H10L12,7L14,2H19L16,10L21,14L18,21H6L3,14L8,10Z','M12,9L14,12L18,13L15,16L16,19L12,17L8,19L9,16L6,13L10,12Z','#FFCA62')
 'ic_action_launch' = @('M12,2L18,10V18L14,16L12,21L10,16L6,18V10Z','M12,5V12M9,10L12,7L15,10M10,19V22M14,19V22','#72E4FF')
 'ic_action_recall' = @('M3,4H21V20H3Z','M8,8L4,12L8,16M4,12H15C21,12 20,6 16,6M8,19H17','#72E4FF')
 'ic_action_sell' = @('M3,5H15L22,12L15,19H3Z','M7,9V15M6,10H8M6,14H8M12,12H18M15,9L18,12L15,15','#FFCA62')
 'ic_product_power_core' = @('M12,2L21,7V17L12,22L3,17V7Z','M13,5L7,13H12L11,19L17,10H12Z','#FFCA62')
 'ic_product_offline_ai' = @('M6,4H18L21,7V18L18,21H6L3,18V7Z','M8,8H16V16H8ZM10,11V13M14,11V13M9,1V4M15,1V4M1,9H3M21,15H23','#72E4FF')
 'ic_product_luck_matrix' = @('M12,2L22,12L12,22L2,12Z','M12,5V19M5,12H19M8,8L16,16M16,8L8,16','#B49CFF')
 'ic_prestige_core' = @('M12,2L21,7V17L12,22L3,17V7Z','M12,5L17,12L12,19L7,12Z','#B49CFF')
 'ic_debris_minimal' = @('M3,9L8,3L17,5L22,13L16,21L5,19Z','M8,3L10,10L3,9M10,10L17,5M10,10L16,21M10,10L22,13','#FFCA62')
 'ic_drone_income_minimal' = @('M3,7L7,3H17L21,7V17L17,21H7L3,17Z','M8,8H16V16H8ZM12,5V8M12,16V19M5,12H8M16,12H19','#72E4FF')
 'ui_language_simple' = @('M12,2A10,10 0,1 0,12 22A10,10 0,1 0,12 2','M2,12H22M4,7H20M4,17H20M12,2C6,7 6,17 12,22C18,17 18,7 12,2','#72E4FF')
 'ui_sound_simple' = @('M3,9H7L13,4V20L7,15H3Z','M16,8C19,10 19,14 16,16M19,5C24,9 24,15 19,19','#72E4FF')
 'ui_motion_simple' = @('M13,3L21,12L13,21L10,18L15,12L10,6Z','M3,6H7M2,12H10M3,18H7','#72E4FF')
 'ui_reset_simple' = @('M12,3A9,9 0,1 1,3 12H6A6,6 0,1 0,12 6Z','M3,3V9H9M3,9L7,5M12,9V13L15,15','#FF6B74')
 'ic_reset_progress' = @('M12,3A9,9 0,1 1,3 12H6A6,6 0,1 0,12 6Z','M3,3V9H9M3,9L7,5M12,9V13L15,15','#FF6B74')
 'ui_lock_simple' = @('M5,10H19L21,12V20L19,22H5L3,20V12Z','M7,10V7A5,5 0,0 1,17 7V10M12,14V18','#B49CFF')
 'ic_space_lock' = @('M5,10H19L21,12V20L19,22H5L3,20V12Z','M7,10V7A5,5 0,0 1,17 7V10M12,14V18','#B49CFF')
 'ui_close_simple' = @('M5,3L12,10L19,3L21,5L14,12L21,19L19,21L12,14L5,21L3,19L10,12L3,5Z','M6,6L18,18M18,6L6,18','#C9E6EF')
 'ui_chevron_right_v2' = @('M7,3L17,12L7,21L4,18L11,12L4,6Z','M7,6L14,12L7,18','#72E4FF')
}
foreach ($entry in $icons.GetEnumerator()) {
 $body,$detail,$accent = $entry.Value
 $metal = '#344C65'
 $edge = '#A6BDCF'
 switch -Regex ($entry.Key) {
  'shop|sell|achievement' { $accent = '#FFD57B'; $metal = '#735035'; $edge = '#D6B57E' }
  'quests|goal|launch' { $accent = '#A6E2B0'; $metal = '#32564F'; $edge = '#A9CBB7' }
  'hangar|power_core|income|motion' { $accent = '#FFC18A'; $metal = '#704B39'; $edge = '#DCC9A4' }
  'prestige|luck|offline' { $accent = '#D6B1F3'; $metal = '#514166'; $edge = '#BDB0D2' }
  'reset' { $accent = '#FFAF9F'; $metal = '#623D43'; $edge = '#D2A6A2' }
  'lock|close|chevron' { $accent = '#D5DCE0'; $metal = '#3E4B58'; $edge = '#AAB6BD' }
 }
 $edge = '#A9BDCF'
 $xml = @"
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#15283C" android:strokeColor="#07101E" android:strokeWidth="1.6" android:strokeLineJoin="round" android:pathData="$body" />
    <path android:fillColor="$metal" android:strokeColor="$edge" android:strokeWidth="0.7" android:strokeLineJoin="round" android:pathData="$body" />
    <path android:fillColor="#00000000" android:strokeColor="#071724" android:strokeWidth="3" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="$detail" />
    <path android:fillColor="#00000000" android:strokeColor="$accent" android:strokeWidth="1.5" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="$detail" />
</vector>
"@
 [IO.File]::WriteAllText((Join-Path $root ($entry.Key + '.xml')), $xml, [Text.UTF8Encoding]::new($false))
}
Write-Output "Updated $($icons.Count) vector control icons."
