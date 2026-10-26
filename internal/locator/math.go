package locator

import "math"

func toRadians(deg float64) float64 { return deg * math.Pi / 180 }
func sin(x float64) float64         { return math.Sin(x) }
func cos(x float64) float64         { return math.Cos(x) }
func sqrt(x float64) float64        { return math.Sqrt(x) }
func atan2(y, x float64) float64    { return math.Atan2(y, x) }
